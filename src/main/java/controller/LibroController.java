package controller;

import dao.CategoriaDAO;
import dao.LibroDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.CategoriaBean;
import models.LibroBean;

/**
 * Controlador de libros.
 * GET  ?accion=nuevo | confirmacion | (sin accion = listar)
 * POST registra un libro (o, con accion=actualizarCantidad, corrige existencias).
 */
@WebServlet("/LibroController")
public class LibroController extends HttpServlet {
    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    // ---- Reglas de validacion ----
    /** Maximo de ejemplares que se puede registrar de un titulo. */
    private static final int MAX_EJEMPLARES = 500;
    /** Titulo: debe contener al menos una letra o numero. */
    private static final Pattern P_TITULO = Pattern.compile(".*[\\p{L}\\p{N}].*");
    /** Autor: empieza con letra; letras, espacios, punto, coma, apostrofe y guion; 2 a 100 caracteres. */
    private static final Pattern P_AUTOR = Pattern.compile("[\\p{L}][\\p{L} .,'\u2019-]{1,99}");
    /** ISBN (opcional, sin guiones): 13 digitos, o 10 (el ultimo puede ser X). */
    private static final Pattern P_ISBN = Pattern.compile("[0-9]{13}|[0-9]{9}[0-9X]");
    /** Nombre de categoria nueva: empieza con letra; letras, espacios, punto, apostrofe y guion; 3 a 80. */
    private static final Pattern P_CATEGORIA = Pattern.compile("[\\p{L}][\\p{L} .'\u2019-]{2,79}");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("nuevo".equals(accion)) {
                mostrarFormulario(req, resp, null);
            } else if ("confirmacion".equals(accion)) {
                // Patron Post/Redirect/Get: el dato guardado viaja en la sesion una sola vez.
                HttpSession ses = req.getSession(false);
                Object l = (ses == null) ? null : ses.getAttribute("confLibro");
                if (l == null) { resp.sendRedirect(req.getContextPath() + "/LibroController"); return; }
                ses.removeAttribute("confLibro");
                req.setAttribute("tipo", "libro");
                req.setAttribute("libro", l);
                req.getRequestDispatcher("confirmacion.jsp").forward(req, resp);
            } else {
                req.setAttribute("libros", libroDAO.listar());
                req.setAttribute("maxEjemplares", MAX_EJEMPLARES);
                req.getRequestDispatcher("listaLibros.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);   // lo muestra error.jsp
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if ("actualizarCantidad".equals(req.getParameter("accion"))) { actualizarCantidad(req, resp); return; }
        List<String> errores = new ArrayList<>();
        String titulo = t(req.getParameter("titulo"));
        String autor = t(req.getParameter("autor"));
        String isbn = t(req.getParameter("isbn")).replaceAll("[ -]", "").toUpperCase();   // acepta 978-607-... y lo normaliza
        String nuevaCategoria = t(req.getParameter("nuevaCategoria"));
        boolean esNueva = "nueva".equals(req.getParameter("idCategoria"));   // eligio "Otra categoria"
        int idCategoria = -1, cantidad = -1;
        try { idCategoria = Integer.parseInt(req.getParameter("idCategoria")); } catch (NumberFormatException e) { }
        try { cantidad = Integer.parseInt(req.getParameter("cantidad").trim()); } catch (NumberFormatException | NullPointerException e) { }

        // 1) Validaciones de formato
        if (titulo.length() < 1 || titulo.length() > 150 || !P_TITULO.matcher(titulo).matches())
            errores.add("El titulo es obligatorio (maximo 150 caracteres) y debe llevar letras o numeros.");
        if (!P_AUTOR.matcher(autor).matches())
            errores.add("El autor debe tener de 2 a 100 caracteres y solo letras, espacios, punto, coma, apostrofe o guion.");
        if (!isbn.isEmpty() && !P_ISBN.matcher(isbn).matches())
            errores.add("El ISBN debe tener 10 o 13 digitos (se aceptan guiones).");
        if (esNueva) {
            if (!P_CATEGORIA.matcher(nuevaCategoria).matches())
                errores.add("El nombre de la nueva categoria debe tener de 3 a 80 caracteres y solo letras y espacios.");
        } else if (idCategoria <= 0) {
            errores.add("Seleccione una categoria.");
        }
        if (cantidad < 0 || cantidad > MAX_EJEMPLARES)
            errores.add("La cantidad debe ser un numero entero entre 0 y " + MAX_EJEMPLARES + ".");

        try {
            // 2) Validaciones contra la base de datos
            if (errores.isEmpty() && !esNueva && !categoriaDAO.existe(idCategoria))
                errores.add("La categoria seleccionada no existe.");
            if (errores.isEmpty() && !isbn.isEmpty() && libroDAO.existeIsbn(isbn))
                errores.add("Ya existe un libro con ese ISBN.");
            if (!errores.isEmpty()) { mostrarFormulario(req, resp, errores); return; }

            if (esNueva) idCategoria = categoriaDAO.registrar(nuevaCategoria);   // crea (o reutiliza) la categoria
            CategoriaBean c = new CategoriaBean();
            c.setIdCategoria(idCategoria);
            LibroBean l = new LibroBean();
            l.setTitulo(titulo);
            l.setAutor(autor);
            l.setIsbn(isbn.isEmpty() ? null : isbn);   // UNIQUE admite varios NULL
            l.setCategoria(c);
            l.setCantidadDisponible(cantidad);
            libroDAO.registrar(l);

            // 3) Guardado correcto: redirigir (si el usuario recarga con F5 NO se vuelve a guardar)
            req.getSession().setAttribute("confLibro", libroDAO.obtenerPorId(l.getIdLibro()));
            resp.sendRedirect(req.getContextPath() + "/LibroController?accion=confirmacion");
        } catch (SQLIntegrityConstraintViolationException e) {      // ISBN repetido por doble envio
            errores.add("Ya existe un libro con ese ISBN.");
            mostrarFormulario(req, resp, errores);
        } catch (SQLException e) {
            getServletContext().log("Error al registrar libro", e);
            errores.add("No se pudo guardar el libro. Verifique la conexion con la base de datos e intente de nuevo.");
            mostrarFormulario(req, resp, errores);
        }
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, List<String> errores)
            throws ServletException, IOException {
        try {
            req.setAttribute("categorias", categoriaDAO.listar());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("errores", errores);
        req.setAttribute("maxEjemplares", MAX_EJEMPLARES);
        req.getRequestDispatcher("registroLibro.jsp").forward(req, resp);
    }

    /** Corrige manualmente las existencias de un libro y vuelve a la lista. */
    private void actualizarCantidad(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = -1, cantidad = -1;
        try {
            id = Integer.parseInt(req.getParameter("idLibro"));
            cantidad = Integer.parseInt(req.getParameter("cantidad").trim());
        } catch (NumberFormatException | NullPointerException e) { }
        String msg = "errorCantidad";
        try {
            if (id > 0 && cantidad >= 0 && cantidad <= MAX_EJEMPLARES && libroDAO.establecerCantidad(id, cantidad)) msg = "cantidad";
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect(req.getContextPath() + "/LibroController?msg=" + msg);
    }

    /** Quita espacios al inicio/final y junta espacios repetidos; null se vuelve cadena vacia. */
    private String t(String s) { return s == null ? "" : s.trim().replaceAll("\\s+", " "); }
}
