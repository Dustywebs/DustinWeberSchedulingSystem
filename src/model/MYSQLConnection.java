package model;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MYSQLConnection {

    private static Connection conn = null;
    //private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB = "U046KE";
    private static final String URL = "jdbc:mysql://52.206.157.109/" + DB;
    private static final String USER = "U046KE";
    private static final String PASS = "53688166290";

    public static Connection getConnection() throws SQLException{

        return conn = DriverManager.getConnection(URL, USER, PASS);
         
    }
    
    /*
        Closing methods
     */
    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
        }
    }

    public static void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
        }
    }
    
    public static void closeStatement(PreparedStatement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
        }
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
        }
    }
}
