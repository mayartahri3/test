package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CoachController implements Initializable {

    @FXML private TableView<Coach> coachTableView;
    @FXML private TableColumn<Coach, Integer> idColumn;
    @FXML private TableColumn<Coach, String> nameColumn;
    @FXML private TableColumn<Coach, String> specialityColumn;
    @FXML private TableColumn<Coach, String> timeOfWorkColumn;
    @FXML private TableColumn<Coach, String> disponibilityColumn;
    @FXML private TableColumn<Coach, String> privateLessonsColumn;
    @FXML private TableColumn<Coach, Void> actionsColumn;

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField specialityField;
    @FXML private ComboBox<String> timeOfWorkComboBox; // Changed from TextField to ComboBox
    @FXML private CheckBox disponibilityCheckBox;
    @FXML private CheckBox privateLessonsCheckBox;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeComboBox;

    @FXML private Button saveButton;
    @FXML private Button clearButton;

    private CoachDAO coachDAO;
    private Coach currentCoach;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        coachDAO = new CoachDAO();
        currentCoach = null;

        // Setup search type combo box
        ObservableList<String> searchTypes = FXCollections.observableArrayList("Name", "Speciality");
        searchTypeComboBox.setItems(searchTypes);
        searchTypeComboBox.setValue("Name");

        // Setup time of work combo box with predefined options
        ObservableList<String> timeOptions = FXCollections.observableArrayList(
                "9:00-17:00", "10:00-14:00", "12:00-20:00", "15:00-21:00", "Flexible"
        );
        timeOfWorkComboBox.setItems(timeOptions);
        timeOfWorkComboBox.setValue("9:00-17:00");

        // Initialize table columns
        initializeTableColumns();

        // Load coach data
        loadCoachData();

        // Setup event handlers
        clearButton.setOnAction(event -> handleClearForm());
        saveButton.setOnAction(event -> handleSaveCoach());

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void initializeTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specialityColumn.setCellValueFactory(new PropertyValueFactory<>("speciality"));
        timeOfWorkColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfWork"));

        disponibilityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isDisponibility() ? "Available" : "Not Available"));

        privateLessonsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isPrivateLessons() ? "Yes" : "No"));

        setupActionsColumn();

        coachTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showCoachDetails(newSelection);
                    }
                });
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Coach, Void>, TableCell<Coach, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Coach, Void> call(final TableColumn<Coach, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    {
                        editButton.setOnAction(event -> {
                            Coach coach = getTableView().getItems().get(getIndex());
                            showCoachDetails(coach);
                        });

                        deleteButton.setOnAction(event -> {
                            Coach coach = getTableView().getItems().get(getIndex());
                            handleDeleteCoach(coach);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttonBox = new HBox(5);
                            Button editBtn = new Button("Edit");
                            Button deleteBtn = new Button("Delete");

                            editBtn.setOnAction(event -> {
                                Coach coach = getTableView().getItems().get(getIndex());
                                showCoachDetails(coach);
                            });

                            deleteBtn.setOnAction(event -> {
                                Coach coach = getTableView().getItems().get(getIndex());
                                handleDeleteCoach(coach);
                            });

                            buttonBox.getChildren().addAll(editBtn, deleteBtn);
                            setGraphic(buttonBox);
                        }
                    }
                };
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }

    private void loadCoachData() {
        coachTableView.setItems(coachDAO.getAllCoaches());
    }

    private void showCoachDetails(Coach coach) {
        currentCoach = coach;
        idField.setText(String.valueOf(coach.getId()));
        nameField.setText(coach.getName());
        specialityField.setText(coach.getSpeciality());
        timeOfWorkComboBox.setValue(coach.getTimeOfWork()); // Update ComboBox value
        disponibilityCheckBox.setSelected(coach.isDisponibility());
        privateLessonsCheckBox.setSelected(coach.isPrivateLessons());
    }

    @FXML
    private void handleSaveCoach() {
        try {
            String name = nameField.getText().trim();
            String speciality = specialityField.getText().trim();
            String timeOfWork = timeOfWorkComboBox.getValue(); // Get selected time
            boolean disponibility = disponibilityCheckBox.isSelected();
            boolean privateLessons = privateLessonsCheckBox.isSelected();

            if (name.isEmpty() || speciality.isEmpty() || timeOfWork == null) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields");
                return;
            }

            if (currentCoach == null) {
                Coach newCoach = new Coach(0, name, speciality, timeOfWork, disponibility, privateLessons);
                coachDAO.addCoach(newCoach);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Coach added successfully");
            } else {
                currentCoach.setName(name);
                currentCoach.setSpeciality(speciality);
                currentCoach.setTimeOfWork(timeOfWork);
                currentCoach.setDisponibility(disponibility);
                currentCoach.setPrivateLessons(privateLessons);
                coachDAO.updateCoach(currentCoach);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Coach updated successfully");
            }

            handleClearForm();
            loadCoachData();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }

    private void handleDeleteCoach(Coach coach) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Coach");
        alert.setContentText("Are you sure you want to delete coach: " + coach.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (coachDAO.deleteCoach(coach.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Coach deleted successfully");
                if (currentCoach != null && currentCoach.getId() == coach.getId()) {
                    handleClearForm();
                }
                loadCoachData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete coach");
            }
        }
    }

    @FXML
    private void handleClearForm() {
        currentCoach = null;
        idField.clear();
        nameField.clear();
        specialityField.clear();
        timeOfWorkComboBox.setValue("9:00-17:00"); // Reset to default
        disponibilityCheckBox.setSelected(false);
        privateLessonsCheckBox.setSelected(false);
        coachTableView.getSelectionModel().clearSelection();
    }

    private void handleSearch(String searchText) {
        ObservableList<Coach> coachList;

        if (searchText == null || searchText.trim().isEmpty()) {
            coachList = coachDAO.getAllCoaches();
        } else {
            String searchType = searchTypeComboBox.getValue();
            if ("Name".equals(searchType)) {
                coachList = FXCollections.observableArrayList(coachDAO.searchCoachesByName(searchText));
            } else {
                coachList = FXCollections.observableArrayList(coachDAO.searchCoachesBySpeciality(searchText));
            }
        }

        coachTableView.setItems(coachList);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}