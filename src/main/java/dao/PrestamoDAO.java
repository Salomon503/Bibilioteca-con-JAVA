package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.*;

public class PrestamoDAO {
    private final LibroDAO libroDAO = new LibroDAO();

    /**
     * Registra el prestamo y descuenta un ejemplar en UNA transaccion.
     * Devuelve false si el libro no tiene ejemplares disponibles.
     */
    public boolean registrar(PrestamoBean p) throws SQLException {
        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);
            try {
                if (!libroDAO.actualizarCantidad(con, p.getLibro().getIdLibro(), -1)) {
                    con.rollback();
                    return false;
                }
                String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, estado) "
                           + "VALUES (?,?,?,?,'Activo')";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, p.getEstudiante().getIdEstudiante());
                    ps.setInt(2, p.getLibro().getIdLibro());
                    ps.setDate(3, p.getFechaPrestamo());
                    ps.setDate(4, p.getFechaDevolucion());
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) p.setIdPrestamo(k.getInt(1)); }
                }
                p.setEstado("Activo");
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /**
     * Lista prestamos con estudiante y libro. Un prestamo 'Activo' cuya fecha de
     * devolucion ya paso se muestra como 'Vencido'.
     */
    public List<PrestamoBean> listar() throws SQLException {
        List<PrestamoBean> lista = new ArrayList<>();
        String sql = "SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_devolucion, "
                   + "CASE WHEN p.estado = 'Activo' AND p.fecha_devolucion < CURDATE() THEN 'Vencido' ELSE p.estado END AS estado, "
                   + "e.id_estudiante, e.carnet, e.nombre_estudiante, l.id_libro, l.titulo, l.autor "
                   + "FROM prestamos p JOIN estudiantes e ON p.id_estudiante = e.id_estudiante "
                   + "JOIN libros l ON p.id_libro = l.id_libro ORDER BY p.id_prestamo DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EstudianteBean e = new EstudianteBean();
                e.setIdEstudiante(rs.getInt("id_estudiante"));
                e.setCarnet(rs.getString("carnet"));
                e.setNombreEstudiante(rs.getString("nombre_estudiante"));
                LibroBean l = new LibroBean();
                l.setIdLibro(rs.getInt("id_libro"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                PrestamoBean p = new PrestamoBean();
                p.setIdPrestamo(rs.getInt("id_prestamo"));
                p.setEstudiante(e);
                p.setLibro(l);
                p.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                p.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                p.setEstado(rs.getString("estado"));
                lista.add(p);
            }
        }
        return lista;
    }

    /** Registra la devolucion: marca 'Devuelto' y repone el ejemplar. False si ya estaba devuelto o no existe. */
    public boolean registrarDevolucion(int idPrestamo) throws SQLException {
        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);
            try {
                int idLibro;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id_libro, estado FROM prestamos WHERE id_prestamo = ? FOR UPDATE")) {
                    ps.setInt(1, idPrestamo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || "Devuelto".equals(rs.getString("estado"))) { con.rollback(); return false; }
                        idLibro = rs.getInt("id_libro");
                    }
                }
                actualizarEstado(con, idPrestamo, "Devuelto");
                libroDAO.actualizarCantidad(con, idLibro, 1);
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** Cambia el estado de un prestamo. */
    public boolean actualizarEstado(int idPrestamo, String estado) throws SQLException {
        try (Connection con = Conexion.getConexion()) {
            return actualizarEstado(con, idPrestamo, estado);
        }
    }

    private boolean actualizarEstado(Connection con, int idPrestamo, String estado) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE prestamos SET estado = ? WHERE id_prestamo = ?")) {
            ps.setString(1, estado);
            ps.setInt(2, idPrestamo);
            return ps.executeUpdate() > 0;
        }
    }

    /** Cantidad de prestamos activos (aun no devueltos) de un estudiante. */
    public int contarActivos(int idEstudiante) throws SQLException {
        return contar("SELECT COUNT(*) FROM prestamos WHERE id_estudiante = ? AND estado = 'Activo'", idEstudiante, null);
    }

    /** Cantidad de prestamos activos cuya fecha de devolucion ya paso (vencidos). */
    public int contarVencidos(int idEstudiante) throws SQLException {
        return contar("SELECT COUNT(*) FROM prestamos WHERE id_estudiante = ? AND estado = 'Activo' "
                    + "AND fecha_devolucion < CURDATE()", idEstudiante, null);
    }

    /** Indica si el estudiante ya tiene ese mismo libro prestado y sin devolver. */
    public boolean tieneActivo(int idEstudiante, int idLibro) throws SQLException {
        return contar("SELECT COUNT(*) FROM prestamos WHERE id_estudiante = ? AND estado = 'Activo' AND id_libro = ?",
                      idEstudiante, idLibro) > 0;
    }

    /** Ejecuta un COUNT(*) con 1 o 2 parametros enteros. */
    private int contar(String sql, int p1, Integer p2) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p1);
            if (p2 != null) ps.setInt(2, p2);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }
}
