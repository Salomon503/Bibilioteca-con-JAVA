package controller;

import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.PrestamoDAO;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.*;

/**
 * Controlador de prestamos.
 * GET  ?accion=nuevo | confirmacion | (sin accion = listar)
 * POST registra un prestamo, o procesa la devolucion con accion=devolver.
 */
@WebServlet("/PrestamoController")
public class PrestamoController extends HttpServlet {
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    // ---- Politica de prestamos (ajustable aqui; todas las fechas se calculan con LocalDate.now(), nunca con un anio fijo) ----
    /** Cuantos dias hacia atras se admite la fecha de prestamo (0 = solo la fecha de hoy). */
    private static final int DIAS_ATRAS_MAX = 0;
    /** Plazo minimo y maximo (en dias) entre la fecha de prestamo y la de devolucion. */
    private static final int DIAS_PRESTAMO_MIN = 1;
    private static final int DIAS_PRESTAMO_MAX = 30;
    /** Maximo de libros que un estudiante puede tener prestados al mismo tiempo. */
    private static final int MAX_PRESTAMOS_ACTIVOS = 3;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("nuevo".equals(accion)) {
                mostrarFormulario(req, resp, null);
            } else if ("confirmacion".equals(accion)) {
                // Patron Post/Redirect/Get: el prestamo guardado viaja en la sesion una sola vez.
                HttpSession ses = req.getSession(false);
                Object p = (ses == null) ? null : ses.getAttribute("confPrestamo");
                if (p == null) { resp.sendRedirect(req.getContextPath() + "/PrestamoController"); return; }
                ses.removeAttribute("confPrestamo");
                req.setAttribute("tipo", "prestamo");
                req.setAttribute("prestamo", p);
                req.getRequestDispatcher("confirmacion.jsp").forward(req, resp);
            } else {
                req.setAttribute("prestamos", prestamoDAO.listar());
                req.getRequestDispatcher("listaPrestamos.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);   // lo muestra error.jsp
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        List<String> errores = new ArrayList<>();
        try {
            if ("devolver".equals(req.getParameter("accion"))) { devolver(req, resp); return; }

            LocalDate hoy = LocalDate.now();
            int idEstudiante = entero(req.getParameter("idEstudiante"));
            int idLibro = entero(req.getParameter("idLibro"));
            LocalDate fp = fecha(req.getParameter("fechaPrestamo"));
            LocalDate fd = fecha(req.getParameter("fechaDevolucion"));

            // 1) Validaciones de formato y de fechas
            if (idEstudiante <= 0) errores.add("Seleccione un estudiante.");
            if (idLibro <= 0) errores.add("Seleccione un libro.");
            if (fp == null) {
                errores.add("Fecha de prestamo invalida.");
            } else if (fp.isAfter(hoy) || fp.isBefore(hoy.minusDays(DIAS_ATRAS_MAX))) {
                errores.add(DIAS_ATRAS_MAX == 0
                        ? "La fecha de prestamo debe ser la de hoy (" + hoy + ")."
                        : "La fecha de prestamo debe estar entre " + hoy.minusDays(DIAS_ATRAS_MAX) + " y " + hoy + ".");
            }
            if (fd == null) {
                errores.add("Fecha de devolucion invalida.");
            } else if (fp != null) {
                long dias = ChronoUnit.DAYS.between(fp, fd);
                if (dias < DIAS_PRESTAMO_MIN)
                    errores.add("La fecha de devolucion debe ser posterior a la fecha de prestamo.");
                else if (dias > DIAS_PRESTAMO_MAX)
                    errores.add("El plazo maximo de prestamo es de " + DIAS_PRESTAMO_MAX + " dias.");
            }

            // 2) Validaciones contra la base de datos (existencia, disponibilidad y reglas de negocio)
            EstudianteBean est = null;
            LibroBean lib = null;
            if (idEstudiante > 0) {
                est = estudianteDAO.obtenerPorId(idEstudiante);
                if (est == null) errores.add("El estudiante seleccionado no existe.");
            }
            if (idLibro > 0) {
                lib = libroDAO.obtenerPorId(idLibro);
                if (lib == null) errores.add("El libro seleccionado no existe.");
                else if (!libroDAO.hayDisponibilidad(idLibro)) errores.add("El libro seleccionado no tiene ejemplares disponibles.");
            }
            if (est != null) {
                if (prestamoDAO.contarVencidos(idEstudiante) > 0)
                    errores.add("El estudiante tiene prestamos vencidos; debe devolverlos antes de pedir otro libro.");
                if (prestamoDAO.contarActivos(idEstudiante) >= MAX_PRESTAMOS_ACTIVOS)
                    errores.add("El estudiante ya tiene " + MAX_PRESTAMOS_ACTIVOS + " prestamos activos (maximo permitido).");
                if (lib != null && prestamoDAO.tieneActivo(idEstudiante, idLibro))
                    errores.add("El estudiante ya tiene prestado este mismo libro y aun no lo devuelve.");
            }
            if (!errores.isEmpty()) { mostrarFormulario(req, resp, errores); return; }

            // 3) Registrar (transaccion: descuenta el ejemplar y guarda el prestamo)
            PrestamoBean p = new PrestamoBean();
            p.setEstudiante(est);
            p.setLibro(lib);
            p.setFechaPrestamo(Date.valueOf(fp));
            p.setFechaDevolucion(Date.valueOf(fd));
            if (!prestamoDAO.registrar(p)) {
                errores.add("No quedan ejemplares disponibles de ese libro.");
                mostrarFormulario(req, resp, errores);
                return;
            }
            p.setLibro(libroDAO.obtenerPorId(idLibro));   // existencias actualizadas para la confirmacion

            // 4) Redirigir (si el usuario recarga con F5 NO se registra otro prestamo)
            req.getSession().setAttribute("confPrestamo", p);
            resp.sendRedirect(req.getContextPath() + "/PrestamoController?accion=confirmacion");
        } catch (SQLException ex) {
            getServletContext().log("Error al registrar prestamo", ex);
            if ("devolver".equals(req.getParameter("accion"))) throw new ServletException(ex);   // lo muestra error.jsp
            errores.add("No se pudo completar la operacion. Verifique la conexion con la base de datos e intente de nuevo.");
            try { mostrarFormulario(req, resp, errores); } catch (SQLException e2) { throw new ServletException(e2); }
        }
    }

    private void devolver(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        boolean ok = prestamoDAO.registrarDevolucion(entero(req.getParameter("idPrestamo")));
        resp.sendRedirect(req.getContextPath() + "/PrestamoController?msg=" + (ok ? "devuelto" : "error"));
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, List<String> errores)
            throws SQLException, ServletException, IOException {
        LocalDate hoy = LocalDate.now();
        req.setAttribute("estudiantes", estudianteDAO.listar());
        req.setAttribute("libros", libroDAO.listarDisponibles());
        // Limites para los campos de fecha del formulario (el servidor vuelve a validarlos)
        req.setAttribute("hoy", hoy.toString());
        req.setAttribute("minPrestamo", hoy.minusDays(DIAS_ATRAS_MAX).toString());
        req.setAttribute("minDevolucion", hoy.plusDays(DIAS_PRESTAMO_MIN).toString());
        req.setAttribute("maxDevolucion", hoy.plusDays(DIAS_PRESTAMO_MAX).toString());
        req.setAttribute("diasMin", DIAS_PRESTAMO_MIN);
        req.setAttribute("diasMax", DIAS_PRESTAMO_MAX);
        req.setAttribute("errores", errores);
        req.getRequestDispatcher("registroPrestamo.jsp").forward(req, resp);
    }

    private int entero(String s) {
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException | NullPointerException e) { return -1; }
    }

    /** Convierte "aaaa-mm-dd" a LocalDate de forma estricta (rechaza 2026-02-31); null si es invalida. */
    private LocalDate fecha(String s) {
        try { return LocalDate.parse(s.trim()); } catch (DateTimeParseException | NullPointerException e) { return null; }
    }
}
