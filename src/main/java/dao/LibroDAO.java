package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.CategoriaBean;
import models.LibroBean;

public class LibroDAO {
    private static final String SELECT =
        "SELECT l.id_libro, l.titulo, l.autor, l.isbn, l.cantidad_disponible, c.id_categoria, c.nombre_categoria "
      + "FROM libros l LEFT JOIN categorias c ON l.id_categoria = c.id_categoria ";

    /** Convierte la fila actual del ResultSet en un LibroBean con su categoria. */
    private LibroBean map(ResultSet rs) throws SQLException {
        CategoriaBean c = new CategoriaBean();
        c.setIdCategoria(rs.getInt("id_categoria"));
        c.setNombreCategoria(rs.getString("nombre_categoria"));
        LibroBean l = new LibroBean();
        l.setIdLibro(rs.getInt("id_libro"));
        l.setTitulo(rs.getString("titulo"));
        l.setAutor(rs.getString("autor"));
        l.setIsbn(rs.getString("isbn"));
        l.setCantidadDisponible(rs.getInt("cantidad_disponible"));
        l.setCategoria(c);
        return l;
    }

    private List<LibroBean> consultar(String sql) throws SQLException {
        List<LibroBean> lista = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    /** Lista todos los libros. */
    public List<LibroBean> listar() throws SQLException {
        return consultar(SELECT + "ORDER BY l.titulo");
    }

    /** Lista solo los libros con ejemplares disponibles (para el select de prestamos). */
    public List<LibroBean> listarDisponibles() throws SQLException {
        return consultar(SELECT + "WHERE l.cantidad_disponible > 0 ORDER BY l.titulo");
    }

    /** Registra un libro. Devuelve true si se inserto. */
    public boolean registrar(LibroBean l) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, isbn, id_categoria, cantidad_disponible) VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getIsbn());
            ps.setInt(4, l.getCategoria().getIdCategoria());
            ps.setInt(5, l.getCantidadDisponible());
            boolean ok = ps.executeUpdate() > 0;
            try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) l.setIdLibro(k.getInt(1)); }
            return ok;
        }
    }

    /** Busca un libro por su id; null si no existe. */
    public LibroBean obtenerPorId(int id) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(SELECT + "WHERE l.id_libro = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    /** Indica si ya existe un libro con ese ISBN. */
    public boolean existeIsbn(String isbn) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM libros WHERE isbn = ?")) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** Valida que el libro tenga al menos un ejemplar disponible. */
    public boolean hayDisponibilidad(int id) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT cantidad_disponible FROM libros WHERE id_libro = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }

    /** Suma delta (negativo = prestar, positivo = devolver). Nunca deja la cantidad bajo cero. */
    public boolean actualizarCantidad(int id, int delta) throws SQLException {
        try (Connection con = Conexion.getConexion()) {
            return actualizarCantidad(con, id, delta);
        }
    }

    /** Version que participa en una transaccion externa. */
    boolean actualizarCantidad(Connection con, int id, int delta) throws SQLException {
        String sql = "UPDATE libros SET cantidad_disponible = cantidad_disponible + ? "
                   + "WHERE id_libro = ? AND cantidad_disponible + ? >= 0";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, id);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        }
    }

    /** Fija la cantidad disponible de un libro (correccion manual de existencias). */
    public boolean establecerCantidad(int id, int cantidad) throws SQLException {
        String sql = "UPDATE libros SET cantidad_disponible = ? WHERE id_libro = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }
}
