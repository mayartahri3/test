package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NavController implements Initializable {

    @FXML private BorderPane mainBorderPane;
    @FXML private StackPane contentArea;
    @FXML private Text headerTitle;
    @FXML private Button dashboardButton;
    @FXML private Button coachesButton;
    @FXML private Button clientsButton;
    @FXML private Button paymentButton;
    @FXML private Button logoutButton;
    @FXML private Text userDisplayName;

    private Connection connection;
    private String username;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("NavController initialized");

        // Set up logout button handler
        if (logoutButton != null) {
            System.out.println("Logout button found and handler set");
            logoutButton.setOnAction(event -> handleLogout());
        } else {
            System.err.println("Logout button not found in FXML!");
        }

        // Check if userDisplayName exists
        if (userDisplayName != null) {
            System.out.println("userDisplayName field found");
        } else {
            System.err.println("userDisplayName field not found in FXML!");
        }
    }

    public void setConnection(Connection connection) {
        System.out.println("Setting connection in NavController: " + connection);
        this.connection = connection;

        // Only load dashboard after connection is set
        if (connection != null) {
            loadDashboard();
        }
    }

    public void setLoginData(String username) {
        System.out.println("Setting username: " + username);
        this.username = username;

        // Update displayed username
        if (userDisplayName != null) {
            userDisplayName.setText(username);
            System.out.println("Username displayed: " + username);
        } else {
            System.err.println("Cannot set username - userDisplayName is null!");
        }
    }

    @FXML
    private void loadDashboard() {
        loadPage("/views/dashboardadmin.fxml");
        setActiveButton(dashboardButton);
        headerTitle.setText("Dashboard");
    }

    @FXML
    private void loadCoaches() {
        loadPage("/views/coaches.fxml");
        setActiveButton(coachesButton);
        headerTitle.setText("Coaches");
    }

    @FXML
    private void loadClients() {
        loadPage("/views/participent.fxml");
        setActiveButton(clientsButton);
        headerTitle.setText("Clients");
    }

    @FXML
    private void loadPayment() {
        loadPage("/views/payement.fxml");
        setActiveButton(paymentButton);
        headerTitle.setText("Payment");
    }

    @FXML
    public void handleLogout() {
        try {
            System.out.println("Logging out...");

            // Close the database connection
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }

            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent loginRoot = loader.load();
            System.out.println("Login FXML loaded");

            // Get current stage and switch scene
            Stage currentStage = (Stage) mainBorderPane.getScene().getWindow();
            Scene scene = new Scene(loginRoot);

            currentStage.setScene(scene);
            currentStage.setWidth(800);  // Reset to login width
            currentStage.setHeight(500); // Reset to login height
            currentStage.centerOnScreen(); // Center the stage
            System.out.println("Switched to login screen");

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPage(String fxml) {
        try {
            System.out.println("Loading page: " + fxml);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();

            // Check if the controller implements Connectable interface and pass connection
            Object controller = loader.getController();
            if (controller instanceof Connectable) {
                System.out.println("Setting connection to " + controller.getClass().getSimpleName());
                ((Connectable) controller).setConnection(connection);
            } else {
                System.out.println("Controller does not implement Connectable: " + controller.getClass().getSimpleName());
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            System.out.println("View loaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + e.getMessage());
        }
    }

    private void setActiveButton(Button activeButton) {
        // Reset all buttons to default style
        String defaultStyle = "-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;";
        String activeStyle = "-fx-background-color: linear-gradient(to right, #0288d1, #4fc3f7); -fx-background-radius: 5;";

        dashboardButton.setStyle(defaultStyle);
        coachesButton.setStyle(defaultStyle);
        clientsButton.setStyle(defaultStyle);
        paymentButton.setStyle(defaultStyle);

        dashboardButton.setTextFill(javafx.scene.paint.Color.valueOf("#424242"));
        coachesButton.setTextFill(javafx.scene.paint.Color.valueOf("#424242"));
        clientsButton.setTextFill(javafx.scene.paint.Color.valueOf("#424242"));
        paymentButton.setTextFill(javafx.scene.paint.Color.valueOf("#424242"));

        // Set active button style
        activeButton.setStyle(activeStyle);
        activeButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    // Interface for controllers that need database connection
    public interface Connectable {
        void setConnection(Connection connection);
    }
}