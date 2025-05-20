package controller;

import Db.Database;
import Db.AdminDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class SignupController implements Initializable {

    @FXML private AnchorPane main;
    @FXML private TextField fullName;
    @FXML private TextField email;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField confirmPassword;
    @FXML private Button signupBtn;
    @FXML private Button cancelBtn;

    private Connection connection;
    private Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up event handlers for the buttons
        signupBtn.setOnAction(this::handleSignup);
        cancelBtn.setOnAction(this::handleCancel);
    }

    /**
     * Set the database connection from the login controller
     * @param connection The database connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Handle the signup button click event
     * @param event The action event
     */
    private void handleSignup(ActionEvent event) {
        // Validate all inputs
        if (!validateInputs()) {
            return;
        }

        // Proceed with registration
        new Thread(() -> {
            try {
                // Check if connection is still valid
                if (connection == null || connection.isClosed()) {
                    connection = Database.getConnection();
                }

                // Check if username already exists
                if (AdminDAO.usernameExists(username.getText())) {
                    Platform.runLater(() -> showAlert(
                            Alert.AlertType.WARNING,
                            "Username Error",
                            "Ce nom d'utilisateur existe déjà. Veuillez en choisir un autre."
                    ));
                    return;
                }

                // Check if email already exists
                if (AdminDAO.emailExists(email.getText())) {
                    Platform.runLater(() -> showAlert(
                            Alert.AlertType.WARNING,
                            "Email Error",
                            "Cette adresse email est déjà utilisée. Veuillez en utiliser une autre."
                    ));
                    return;
                }

                // Register the new admin
                boolean success = AdminDAO.registerAdmin(
                        fullName.getText(),
                        email.getText(),
                        username.getText(),
                        password.getText()
                );

                if (success) {
                    Platform.runLater(() -> {
                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Registration Success",
                                "Compte créé avec succès! Vous pouvez maintenant vous connecter."
                        );
                        closeStage();
                    });
                } else {
                    Platform.runLater(() -> showAlert(
                            Alert.AlertType.ERROR,
                            "Registration Error",
                            "Une erreur est survenue lors de la création du compte."
                    ));
                }
            } catch (SQLException e) {
                Platform.runLater(() -> showAlert(
                        Alert.AlertType.ERROR,
                        "Database Error",
                        "Erreur de base de données: " + e.getMessage()
                ));
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Validate all input fields
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        // Check if any field is empty
        if (fullName.getText().isEmpty() ||
                email.getText().isEmpty() ||
                username.getText().isEmpty() ||
                password.getText().isEmpty() ||
                confirmPassword.getText().isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Input Error",
                    "Tous les champs sont obligatoires."
            );
            return false;
        }

        // Validate email format
        if (!emailPattern.matcher(email.getText()).matches()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Email Error",
                    "Veuillez entrer une adresse email valide."
            );
            return false;
        }

        // Validate password length
        if (password.getText().length() < 6) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Password Error",
                    "Le mot de passe doit contenir au moins 6 caractères."
            );
            return false;
        }

        // Check if passwords match
        if (!password.getText().equals(confirmPassword.getText())) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Password Error",
                    "Les mots de passe ne correspondent pas."
            );
            return false;
        }

        return true;
    }

    /**
     * Handle the cancel button click event
     * @param event The action event
     */

    private void handleCancel(ActionEvent event) {
        closeStage();
    }

    /**
     * Close the current stage
     */
    private void closeStage() {
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    /**
     * Show an alert dialog
     * @param alertType The type of alert
     * @param title The title of the alert
     * @param message The message of the alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Style the alert dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/views/logindesign.css").toExternalForm());
            dialogPane.getStyleClass().add("modern-dialog");

            alert.showAndWait();
        });
    }
}