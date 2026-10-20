package controller;

import dao.EstudianteDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.EstudianteBean;

/**
 * Controlador de estudiantes.
 * GET  ?accion=nuevo | confirmacion | (sin accion = listar)
 * POST registra al estudiante (valida, guarda y REDIRIGE a la confirmacion).
 */
@WebServlet("/EstudianteController")
public class EstudianteController extends HttpServlet {
    private final EstudianteDAO dao = new EstudianteDAO();

    // ---- Reglas de validacion (ningun valor depende del anio actual) ----
    /** Carne: 2 letras + 4 a 8 digitos (6 a 10 caracteres en total; la columna es VARCHAR(10)). */
    private static final Pattern P_CARNET = Pattern.compile("[A-Za-z]{2}[0-9]{4}[0-9]{0,4}");
    /** Nombre: empieza con letra; letras, espacios, punto, apostrofe y guion; 3 a 100 caracteres. */
    private static final Pattern P_NOMBRE = Pattern.compile("[\\p{L}][\\p{L} .'\u2019-]{2,99}");
    /** Carrera: empieza con letra; letras, espacios y punto; 3 a 80 caracteres. */
    private static final Pattern P_CARRERA = Pattern.compile("[\\p{L}][\\p{L} .]{2,79}");
    /** Telefono (opcional): 8 digitos con guion; los numeros de El Salvador inician con 2, 6 o 7. */
    private static final Pattern P_TELEFONO = Pattern.compile("[267][0-9]{3}-[0-9]{4}");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if ("nuevo".equals(accion)) {
            req.getRequestDispatcher("registroEstudiante.jsp").forward(req, resp);
            return;
        }
        if ("confirmacion".equals(accion)) {
            // Patron Post/Redirect/Get: el dato guardado viaja en la sesion una sola vez.
            HttpSession ses = req.getSession(false);
            Object e = (ses == null) ? null : ses.getAttribute("confEstudiante");
            if (e == null) { resp.sendRedirect(req.getContextPath() + "/EstudianteController"); return; }
            ses.removeAttribute("confEstudiante");
            req.setAttribute("tipo", "estudiante");
            req.setAttribute("estudiante", e);
            req.getRequestDispatcher("confirmacion.jsp").forward(req, resp);
            return;
        }
        try {
            req.setAttribute("estudiantes", dao.listar());
            req.getRequestDispatcher("listaEstudiantes.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);   // lo muestra error.jsp
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        List<String> errores = new ArrayList<>();
        String carnet = t(req.getParameter("carnet")).toUpperCase();
        String nombre = t(req.getParameter("nombre"));
        String carrera = t(req.getParameter("carrera"));
        String telefono = t(req.getParameter("telefono"));

        // 1) Validaciones de formato
        if (!P_CARNET.matcher(carnet).matches())
            errores.add("El carne debe tener 2 letras seguidas de 4 a 8 numeros (ej. AB210001).");
        if (!P_NOMBRE.matcher(nombre).matches())
            errores.add("El nombre debe tener de 3 a 100 caracteres y solo letras, espacios, punto, apostrofe o guion.");
        if (!P_CARRERA.matcher(carrera).matches())
            errores.add("La carrera debe tener de 3 a 80 caracteres y solo letras, espacios y punto.");
        if (!telefono.isEmpty() && !P_TELEFONO.matcher(telefono).matches())
            errores.add("El telefono debe tener el formato 7777-7777 y empezar con 2, 6 o 7.");

        try {
            // 2) Validacion contra la base de datos (carne repetido)
            if (errores.isEmpty() && dao.existeCarnet(carnet)) errores.add("Ya existe un estudiante con ese carne.");
            if (!errores.isEmpty()) {
                req.setAttribute("errores", errores);
                req.getRequestDispatcher("registroEstudiante.jsp").forward(req, resp);
                return;
            }
            EstudianteBean e = new EstudianteBean();
            e.setCarnet(carnet);
            e.setNombreEstudiante(nombre);
            e.setCarrera(carrera);
            e.setTelefono(telefono.isEmpty() ? null : telefono);
            dao.registrar(e);

            // 3) Guardado correcto: redirigir (si el usuario recarga con F5 NO se vuelve a guardar)
            req.getSession().setAttribute("confEstudiante", e);
            resp.sendRedirect(req.getContextPath() + "/EstudianteController?accion=confirmacion");
        } catch (SQLIntegrityConstraintViolationException ex) {      // carne repetido por doble envio
            errores.add("Ya existe un estudiante con ese carne.");
            req.setAttribute("errores", errores);
            req.getRequestDispatcher("registroEstudiante.jsp").forward(req, resp);
        } catch (SQLException ex) {
            getServletContext().log("Error al registrar estudiante", ex);
            errores.add("No se pudo guardar el estudiante. Verifique la conexion con la base de datos e intente de nuevo.");
            req.setAttribute("errores", errores);
            req.getRequestDispatcher("registroEstudiante.jsp").forward(req, resp);
        }
    }

    /** Quita espacios al inicio/final y junta espacios repetidos; null se vuelve cadena vacia. */
    private String t(String s) { return s == null ? "" : s.trim().replaceAll("\\s+", " "); }
}
