package controller;

import Db.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Coach entities
 * Interacts with MySQL database
 */
public class CoachDAO {
    // CREATE operation
    public void addCoach(Coach coach) {
        String sql = "INSERT INTO coach (name, speciality, time_of_work, disponibility, private_lessons) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, coach.getName());
            pstmt.setString(2, coach.getSpeciality());
            pstmt.setString(3, coach.getTimeOfWork());
            pstmt.setBoolean(4, coach.isDisponibility());
            pstmt.setBoolean(5, coach.isPrivateLessons());

            pstmt.executeUpdate();

            // Retrieve the auto-generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    coach.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ operations
    public ObservableList<Coach> getAllCoaches() {
        ObservableList<Coach> coaches = FXCollections.observableArrayList();
        String sql = "SELECT * FROM coach";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Coach coach = new Coach(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("speciality"),
                        rs.getString("time_of_work"),
                        rs.getBoolean("disponibility"),
                        rs.getBoolean("private_lessons")
                );
                coaches.add(coach);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coaches;
    }

    // Get coach by ID
    public Coach getCoachById(int id) {
        String sql = "SELECT * FROM coach WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Coach(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("speciality"),
                            rs.getString("time_of_work"),
                            rs.getBoolean("disponibility"),
                            rs.getBoolean("private_lessons")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // UPDATE operation
    public boolean updateCoach(Coach coach) {
        String sql = "UPDATE coach SET name = ?, speciality = ?, time_of_work = ?, disponibility = ?, private_lessons = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, coach.getName());
            pstmt.setString(2, coach.getSpeciality());
            pstmt.setString(3, coach.getTimeOfWork());
            pstmt.setBoolean(4, coach.isDisponibility());
            pstmt.setBoolean(5, coach.isPrivateLessons());
            pstmt.setInt(6, coach.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE operation
    public boolean deleteCoach(int id) {
        String sql = "DELETE FROM coach WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search coaches by name
    public List<Coach> searchCoachesByName(String name) {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT * FROM coach WHERE name LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Coach coach = new Coach(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("speciality"),
                            rs.getString("time_of_work"),
                            rs.getBoolean("disponibility"),
                            rs.getBoolean("private_lessons")
                    );
                    coaches.add(coach);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coaches;
    }

    // Search coaches by speciality
    public List<Coach> searchCoachesBySpeciality(String speciality) {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT * FROM coach WHERE speciality LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + speciality + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Coach coach = new Coach(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("speciality"),
                            rs.getString("time_of_work"),
                            rs.getBoolean("disponibility"),
                            rs.getBoolean("private_lessons")
                    );
                    coaches.add(coach);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coaches;
    }
}