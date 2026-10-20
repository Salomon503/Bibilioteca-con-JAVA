package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.CategoriaBean;

public class CategoriaDAO {
    /** Obtiene todas las categorias ordenadas por nombre. */
    public List<CategoriaBean> listar() throws SQLException {
        List<CategoriaBean> lista = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre_categoria FROM categorias ORDER BY nombre_categoria";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CategoriaBean c = new CategoriaBean();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                lista.add(c);
            }
        }
        return lista;
    }

    /** Registra una categoria nueva y devuelve su id. Si el nombre ya existe, devuelve el id existente. */
    public int registrar(String nombre) throws SQLException {
        try (Connection con = Conexion.getConexion()) {
            String buscar = "SELECT id_categoria FROM categorias WHERE LOWER(nombre_categoria) = LOWER(?)";
            try (PreparedStatement ps = con.prepareStatement(buscar)) {
                ps.setString(1, nombre);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
            }
            String sql = "INSERT INTO categorias (nombre_categoria) VALUES (?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : -1; }
            }
        }
    }

    /** Indica si existe una categoria con ese id (evita errores de llave foranea). */
    public boolean existe(int id) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM categorias WHERE id_categoria = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}
