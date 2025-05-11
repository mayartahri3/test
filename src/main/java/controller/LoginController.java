package controller;

import Db.Database;
import Db.AdminDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private StackPane main;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Button loginbtn;

    private StringBuilder actualPassword = new StringBuilder();
    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPasswordField();
        loginbtn.setOnAction(this::handleLogin);

        // Initialize database connection
        initializeDatabaseConnection();
    }

    private void initializeDatabaseConnection() {
        new Thread(() -> {
            try {
                connection = Database.getConnection();
                if (connection == null || connection.isClosed()) {
                    Platform.runLater(() -> showAlert(
                            Alert.AlertType.ERROR,
                            "Database Error",
                            "Failed to establish database connection"
                    ));
                }
            } catch (SQLException e) {
                Platform.runLater(() -> showAlert(
                        Alert.AlertType.ERROR,
                        "Database Error",
                        "Connection failed: " + e.getMessage()
                ));
            }
        }).start();
    }

    private void handleLogin(ActionEvent event) {
        String usernameText = username.getText();
        String passwordText = actualPassword.toString();

        if (usernameText.isEmpty() || passwordText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter both username and password");
            shakeFields();
            return;
        }

        new Thread(() -> {
            try {
                // Verify connection is still valid
                if (connection == null || connection.isClosed()) {
                    connection = Database.getConnection();
                }

                if (AdminDAO.authenticateAdmin(usernameText, passwordText)) {
                    Platform.runLater(() -> loadDashboard());
                } else {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials");
                        shakeFields();
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Login Error",
                            "Error during authentication: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboardadmin.fxml"));
            Parent dashboardRoot = loader.load();

            // Get the controller and pass the connection
            DashboardController dashboardController = loader.getController();
            dashboardController.setConnection(this.connection);

            // Get current stage and switch scene
            Stage currentStage = (Stage) main.getScene().getWindow();
            currentStage.setScene(new Scene(dashboardRoot));

            // Set close handler
            currentStage.setOnCloseRequest(e -> {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    System.err.println("Error closing connection: " + ex.getMessage());
                }
            });

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void shakeFields() {
        // Simple visual feedback - you can implement a more sophisticated animation
        username.setStyle("-fx-border-color: red;");
        password.setStyle("-fx-border-color: red;");

        // Reset styles after a delay
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    username.setStyle("");
                    password.setStyle("");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void setupPasswordField() {
        // Handle key typing in the password field
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            // Don't process if we're programmatically updating the field
            if (oldValue == null || newValue == null) return;

            // Get the current caret position for proper placement after we modify the text
            int caretPos = password.getCaretPosition();

            // Handle backspace - remove the last character from actualPassword
            if (newValue.length() < oldValue.length()) {
                if (actualPassword.length() > 0) {
                    actualPassword.deleteCharAt(actualPassword.length() - 1);
                }
                // Update the display with the correct number of asterisks
                password.setText("*".repeat(actualPassword.length()));
                password.positionCaret(caretPos);
                return;
            }

            // Handle added character
            if (newValue.length() > oldValue.length() && !newValue.matches("\\*+")) {
                // Find the new character that was added
                String addedChar = "";
                for (int i = 0; i < newValue.length(); i++) {
                    if (i >= oldValue.length() || newValue.charAt(i) != oldValue.charAt(i)) {
                        addedChar = String.valueOf(newValue.charAt(i));
                        break;
                    }
                }

                // Add the new character to our actual password
                actualPassword.append(addedChar);

                // Show only asterisks in the field
                password.setText("*".repeat(actualPassword.length()));

                // Position the caret at the correct position
                password.positionCaret(caretPos);
            }
        });
    }
}