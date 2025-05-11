package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class DashboardController {

    @FXML private Label usersCountLabel;
    @FXML private Label paymentsLabel;
    @FXML private Label coachesLabel;
    @FXML private LineChart<String, Number> profitChart;
    @FXML private Circle userAvatarCircle;  // Changed to match the Circle in FXML

    // Navigation buttons
    @FXML private Button dashboardButton;
    @FXML private Button addCoachesButton;
    @FXML private Button addParticipantButton;
    @FXML private Button paymentButton;
    @FXML private Button settingsButton;

    private Connection connection;

    public void setConnection(Connection connection) {
        System.out.println("Setting connection in DashboardController: " + connection);
        this.connection = connection;
        if (connection != null) {
            try {
                System.out.println("Connection valid? " + !connection.isClosed());
            } catch (SQLException e) {
                System.err.println("Error checking connection: " + e.getMessage());
            }
        }
        loadDashboardData();
    }



    public void cleanup() {
        // This will be called when window closes
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }}

    @FXML
    public void initialize() {
        // Set up button actions
        dashboardButton.setOnAction(e -> switchToDashboard());
        addCoachesButton.setOnAction(e -> switchToCoaches());
        addParticipantButton.setOnAction(e -> switchToParticipants());
        paymentButton.setOnAction(e -> switchToPayments());
        settingsButton.setOnAction(e -> openSettings());

        loadDashboardData();

        // You can set an image to the circle using a ImageView inside it if needed
        // userAvatarCircle.setFill(new ImagePattern(new Image("/images/default-admin.png")));
    }

    private void loadDashboardData() {
        try {
            // Load dashboard statistics
            int usersCount = fetchUsersCount();
            usersCountLabel.setText(String.valueOf(usersCount));

            double totalPayments = fetchTotalPayments();
            paymentsLabel.setText(String.format("$%.2f", totalPayments));

            int coachesCount = fetchCoachesCount();
            coachesLabel.setText(String.valueOf(coachesCount));

            // Load chart data
            loadChartData();

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }
    }

    // Navigation methods
    @FXML
    public void switchToDashboard() {
       loadDashboardData();
    }

    @FXML
    public void switchToCoaches() {
        loadView("/views/coaches.fxml");
    }

    @FXML
    public void switchToParticipants() {
        loadView("/views/participent.fxml");
    }

    @FXML
    public void switchToPayments() {
        loadView("/views/payement.fxml");
    }

    @FXML
    public void openSettings() {
        loadView("/views/setting.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            System.out.println("Attempting to load: " + fxmlPath);
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            System.out.println("FXML URL: " + url);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) dashboardButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
            // Show alert to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load view");
            alert.setContentText("Could not load the requested view: " + fxmlPath);
            alert.showAndWait();
        }
    }




    // Database methods (implement these based on your DB schema)
    private int fetchUsersCount() {
        // Sample implementation
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM users");
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return 0;
        }
    }

    private double fetchTotalPayments() {
        // Sample implementation
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT SUM(amount) FROM payments");
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return 0.0;
        }
    }

    private int fetchCoachesCount() {
        // Sample implementation
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM coaches");
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return 0;
        }
    }

    private void loadChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Jan", 2500));
        series.getData().add(new XYChart.Data<>("Feb", 3000));
        series.getData().add(new XYChart.Data<>("Mar", 2800));
        // Add more months...
        profitChart.getData().add(series);
    }
}