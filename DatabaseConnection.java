package byod.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection manager.
 * Update DB_URL, DB_USER, DB_PASS to match your MySQL setup.
 */
public class DatabaseConnection {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/byod_db?useSSL=false&serverTimezone=Asia/Manila";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "yourpassword";

    private static Connection instance;

    private DatabaseConnection() {}

    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                System.out.println("Database connected successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add connector JAR to classpath.", e);
            }
        }
        return instance;
    }

    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
