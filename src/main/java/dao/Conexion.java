package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Fabrica de conexiones JDBC. Ajuste USER y PASS segun su MySQL. */
public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3307/bibliotecaudb"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/El_Salvador";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
