package Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class AdminDAO {


    public static boolean authenticateAdmin(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed during authentication");
                return false;
            }

            // Query to get the admin with the given username and password
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            // If a record exists with matching credentials, authentication is successful
            boolean authenticated = rs.next();

            // If authentication was successful, update last login timestamp
            if (authenticated) {
                updateLastLogin(username, conn);
            }

            return authenticated;

        } catch (SQLException e) {
            System.err.println("SQL error during authentication: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean createAdmin(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed during admin creation");
                return false;
            }

            // Check if admin already exists
            if (adminExists(username, conn)) {
                System.out.println("Admin already exists: " + username);
                return false;
            }

            // Query to insert new admin
            String query = "INSERT INTO admin (username, password) VALUES (?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("SQL error during admin creation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private static void updateLastLogin(String username, Connection conn) {
        PreparedStatement stmt = null;

        try {
            String query = "UPDATE admin SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // Get users registered this month
    public static int getUsersThisMonth() {
        String query = "SELECT COUNT(*) FROM users WHERE MONTH(registration_date) = MONTH(CURRENT_DATE())";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Get total payments
    public static double getTotalPayments() {
        String query = "SELECT SUM(amount) FROM payments";
        try (Connection conn =Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Get coach count
    public static int getCoachesCount() {
        String query = "SELECT COUNT(*) FROM coaches";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Get monthly profits (example: returns hardcoded data)
    public static Map<String, Number> getMonthlyProfits() {
        return Map.of(
                "Jan", 1500,
                "Feb", 2200,
                "Mar", 1800,
                "Apr", 3000
        );
    }
    private static boolean adminExists(String username, Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id FROM admin WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking if admin exists: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}