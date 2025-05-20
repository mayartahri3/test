package Db;

import controller.RegistrationModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperParticipent {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/dashboard";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insert(String name, String email, String phone, String membership,Date join_date) {
        String sql = "INSERT INTO users (name, email, phone, membership, join_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, membership);
            pstmt.setDate(5, join_date);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error adding user: " + e.getMessage());
        }
    }

    public static void update(int id, String name, String email, String phone, String membership,Date join_date) {
        String sql = "UPDATE users SET name=?, email=?, phone=?, membership=?, join_date=? WHERE id=?";;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, membership);
            pstmt.setInt(6, id);
            pstmt.setDate(5, join_date);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error updating user: " + e.getMessage());
        }
    }

    public static void delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error deleting user: " + e.getMessage());
        }
    }

    public static List<RegistrationModel> getAllUsers() {
        List<RegistrationModel> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String membership = rs.getString("membership");
                String joinDate = rs.getString("join_date");

                users.add(new RegistrationModel(id, name, email, phone, membership, joinDate));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error retrieving users: " + e.getMessage());
        }

        return users;
    }

    private static void showError(String message) {
        System.err.println(message);
    }
}