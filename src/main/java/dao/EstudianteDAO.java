package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.EstudianteBean;

public class EstudianteDAO {
    private EstudianteBean map(ResultSet rs) throws SQLException {
        EstudianteBean e = new EstudianteBean();
        e.setIdEstudiante(rs.getInt("id_estudiante"));
        e.setCarnet(rs.getString("carnet"));
        e.setNombreEstudiante(rs.getString("nombre_estudiante"));
        e.setCarrera(rs.getString("carrera"));
        e.setTelefono(rs.getString("telefono"));
        return e;
    }

    /** Lista todos los estudiantes. */
    public List<EstudianteBean> listar() throws SQLException {
        List<EstudianteBean> lista = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes ORDER BY nombre_estudiante";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    /** Registra un estudiante. */
    public boolean registrar(EstudianteBean e) throws SQLException {
        String sql = "INSERT INTO estudiantes (carnet, nombre_estudiante, carrera, telefono) VALUES (?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getCarnet());
            ps.setString(2, e.getNombreEstudiante());
            ps.setString(3, e.getCarrera());
            ps.setString(4, e.getTelefono());
            boolean ok = ps.executeUpdate() > 0;
            try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) e.setIdEstudiante(k.getInt(1)); }
            return ok;
        }
    }

    public boolean existeCarnet(String carnet) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM estudiantes WHERE carnet = ?")) {
            ps.setString(1, carnet);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public EstudianteBean obtenerPorId(int id) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM estudiantes WHERE id_estudiante = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
}
