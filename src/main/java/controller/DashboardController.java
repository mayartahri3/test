package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

// Implement the Connectable interface
public class DashboardController implements NavController.Connectable {

    @FXML private Label usersCountLabel;
    @FXML private Label paymentsLabel;
    @FXML private Label coachesLabel;
    @FXML private LineChart<String, Number> profitChart;
    @FXML private Button dashboardButton;
    @FXML private Button addCoachesButton;
    @FXML private Button addParticipantButton;
    @FXML private Button paymentButton;
    @FXML private Button settingsButton;
    @FXML private Button refreshButton;

    // These may be null if not in the FXML
    @FXML private TableView<CoachSchedule> scheduleTable;
    @FXML private TableColumn<CoachSchedule, String> coachNameColumn;
    @FXML private TableColumn<CoachSchedule, String> classTypeColumn;
    @FXML private TableColumn<CoachSchedule, String> timeColumn;
    @FXML private TableColumn<CoachSchedule, String> isPrivateColumn;

    private Connection connection;

    // Class to hold coach schedule data
    public static class CoachSchedule {
        private final SimpleStringProperty coachName;
        private final SimpleStringProperty classType;
        private final SimpleStringProperty time;
        private final SimpleStringProperty isPrivate;

        public CoachSchedule(String coachName, String classType, String time, String isPrivate) {
            this.coachName = new SimpleStringProperty(coachName);
            this.classType = new SimpleStringProperty(classType);
            this.time = new SimpleStringProperty(time);
            this.isPrivate = new SimpleStringProperty(isPrivate);
        }

        public String getCoachName() { return coachName.get(); }
        public SimpleStringProperty coachNameProperty() { return coachName; }
        public String getClassType() { return classType.get(); }
        public SimpleStringProperty classTypeProperty() { return classType; }
        public String getTime() { return time.get(); }
        public SimpleStringProperty timeProperty() { return time; }
        public String getIsPrivate() { return isPrivate.get(); }
        public SimpleStringProperty isPrivateProperty() { return isPrivate; }
    }

    @Override
    public void setConnection(Connection connection) {
        System.out.println("Setting connection in DashboardController: " + connection);
        this.connection = connection;
        if (connection != null) {
            try {
                System.out.println("Connection valid? " + !connection.isClosed());
                // Only load data after connection is set
                loadDashboardData();
            } catch (SQLException e) {
                System.err.println("Error checking connection: " + e.getMessage());
            }
        }
    }

    public void cleanup() {
        // Don't close the connection here - the NavController manages it
        // Just release any resources specific to this controller
    }

    @FXML
    public void initialize() {
        // Set up table columns if they exist
        if (scheduleTable != null && coachNameColumn != null) {
            coachNameColumn.setCellValueFactory(cellData -> cellData.getValue().coachNameProperty());
            classTypeColumn.setCellValueFactory(cellData -> cellData.getValue().classTypeProperty());
            timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
            isPrivateColumn.setCellValueFactory(cellData -> cellData.getValue().isPrivateProperty());
        }

        // Don't call loadDashboardData() here anymore
        // It will be called by setConnection()

        // Only set up refresh button if it exists
        if (refreshButton != null) {
            refreshButton.setOnAction(event -> loadDashboardData());
        }
    }

    private void loadDashboardData() {
        try {
            if (connection == null) {
                System.out.println("Database connection not established yet");
                return;
            }

            if (usersCountLabel != null) {
                int usersCount = fetchUsersCount();
                usersCountLabel.setText(String.valueOf(usersCount));
            }

            if (paymentsLabel != null) {
                double totalPayments = fetchTotalPayments();
                paymentsLabel.setText(String.format("$%.2f", totalPayments));
            }

            if (coachesLabel != null) {
                int coachesCount = fetchCoachesCount();
                coachesLabel.setText(String.valueOf(coachesCount));
            }

            if (profitChart != null) {
                loadChartData();
            }

            if (scheduleTable != null) {
                loadScheduleData();
            }

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more detailed error info
        }
    }

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

            // Check if dashboardButton exists before using it
            Scene currentScene = null;
            if (dashboardButton != null) {
                currentScene = dashboardButton.getScene();
            } else if (profitChart != null) {
                currentScene = profitChart.getScene();
            }

            if (currentScene != null) {
                Stage stage = (Stage) currentScene.getWindow();
                stage.setScene(new Scene(root));
                stage.show();

                // Pass connection to new controller if it implements Connectable
                Object controller = loader.getController();
                if (controller instanceof NavController.Connectable) {
                    ((NavController.Connectable) controller).setConnection(connection);
                }
            } else {
                throw new IOException("Cannot find current scene to switch view");
            }
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load view");
            alert.setContentText("Could not load the requested view: " + fxmlPath);
            alert.showAndWait();
        }
    }

    private int fetchUsersCount() {
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
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT SUM(montant) FROM paiements");
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return 0.0;
        }
    }

    private int fetchCoachesCount() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM coach");
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return 0;
        }
    }

    private void loadChartData() {
        profitChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Profit");
        series.getData().add(new XYChart.Data<>("Jan", 2500));
        series.getData().add(new XYChart.Data<>("Feb", 3000));
        series.getData().add(new XYChart.Data<>("Mar", 2800));
        series.getData().add(new XYChart.Data<>("Apr", 3200));
        series.getData().add(new XYChart.Data<>("May", 2900));
        profitChart.getData().add(series);
    }

    private void loadScheduleData() {
        if (scheduleTable == null) {
            System.err.println("scheduleTable is null");
            return;
        }

        ObservableList<CoachSchedule> data = FXCollections.observableArrayList();
        try {
            if (connection == null || connection.isClosed()) {
                System.err.println("Database connection is null or closed");
                return;
            }

            String query = "SELECT name, class_type, class_time, is_private FROM coach WHERE class_date = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now())); // Use Date for DATE columns
            ResultSet rs = stmt.executeQuery();

            int rowCount = 0;
            while (rs.next()) {
                String coachName = rs.getString("name") != null ? rs.getString("name") : "";
                String classType = rs.getString("class_type") != null ? rs.getString("class_type") : "";
                String time = rs.getString("class_time") != null ? rs.getString("class_time") : "";
                String isPrivate = rs.getString("is_private") != null ? rs.getString("is_private") : "";
                data.add(new CoachSchedule(coachName, classType, time, isPrivate));
                rowCount++;
            }
            scheduleTable.setItems(data);
            scheduleTable.refresh();
            System.out.println("Loaded " + rowCount + " schedule entries");

            // Set placeholder if no data
            if (rowCount == 0) {
                scheduleTable.setPlaceholder(new Label("No classes scheduled for today"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading schedule data: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to load schedule");
            alert.setContentText("Could not load coach schedule data: " + e.getMessage());
            alert.showAndWait();
        }
    }
}