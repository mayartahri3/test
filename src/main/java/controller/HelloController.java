package controller;

import Db.DatabaseHelperParticipent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HelloController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> membershipCombo;
    @FXML private TextField healthField;
    @FXML private TextField joinDateField;
    // Emergency contact fields
    @FXML private TextField emergencyNameField;
    @FXML private TextField emergencyPhoneField;
    @FXML private TextField relationshipField;

    @FXML private TableView<RegistrationModel> tableView;
    @FXML private TableColumn<RegistrationModel, Integer> colId;
    @FXML private TableColumn<RegistrationModel, String> colName;
    @FXML private TableColumn<RegistrationModel, String> colEmail;
    @FXML private TableColumn<RegistrationModel, String> colPhone;
    @FXML private TableColumn<RegistrationModel, String> colMembership;
    @FXML private TableColumn<RegistrationModel, String> colJoinDate;

    @FXML private Label nameError;
    @FXML private Label emailError;
    @FXML private Label phoneError;
    @FXML private Label membershipError;
    @FXML private Label joinDateError;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Combo box options - matching the values in the database
        membershipCombo.setItems(FXCollections.observableArrayList("Premium", "Standard", "Basic"));

        // Table column setup
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colMembership.setCellValueFactory(new PropertyValueFactory<>("membership"));
        colJoinDate.setCellValueFactory(new PropertyValueFactory<>("joinDate"));

        // Set default join date to today
        setDefaultJoinDate();

        // Add selection listener to populate form when table row is selected
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFormWithSelection(newSelection);
            }
        });

        refreshTable();
    }

    private void setDefaultJoinDate() {
        // Set default join date to today's date
        joinDateField.setText(LocalDate.now().format(dateFormatter));
    }

    private void populateFormWithSelection(RegistrationModel user) {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        membershipCombo.setValue(user.getMembership());
        joinDateField.setText(user.getJoinDate());

        // We can't populate these fields as they're not in the database yet
        healthField.clear();
        emergencyNameField.clear();
        emergencyPhoneField.clear();
        relationshipField.clear();
    }

    private boolean validateForm() {
        boolean valid = true;

        // Clear errors
        nameError.setText("");
        emailError.setText("");
        phoneError.setText("");
        membershipError.setText("");
        if (joinDateError != null) joinDateError.setText("");

        if (nameField.getText().isEmpty()) {
            nameError.setText("Name is required");
            valid = false;
        }
        if (emailField.getText().isEmpty()) {
            emailError.setText("Email is required");
            valid = false;
        } else if (!emailField.getText().contains("@")) {
            emailError.setText("Invalid email format");
            valid = false;
        }
        if (phoneField.getText().isEmpty()) {
            phoneError.setText("Phone is required");
            valid = false;
        }
        if (membershipCombo.getValue() == null) {
            membershipError.setText("Membership is required");
            valid = false;
        }

        // Validate join date format
        String joinDate = joinDateField.getText();
        if (!joinDate.isEmpty()) {
            try {
                // Try to parse the date
                LocalDate.parse(joinDate, dateFormatter);
            } catch (DateTimeParseException e) {
                if (joinDateError != null) {
                    joinDateError.setText("Invalid date format. Use YYYY-MM-DD");
                } else {
                    showAlert("Invalid date format. Please use YYYY-MM-DD");
                }
                valid = false;
            }
        }

        return valid;
    }

    @FXML
    private void handleSubmit() {
        if (!validateForm()) return;

        DatabaseHelperParticipent.insert(
                nameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                membershipCombo.getValue(),
                Date.valueOf(joinDateField.getText())
        );

        refreshTable();
        clearFields();
        setDefaultJoinDate();
        showAlert("User registered successfully!");
    }

    @FXML
    private void handleDelete() {
        RegistrationModel selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Confirm deletion with dialog
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Deletion");
            confirmDialog.setHeaderText("Delete User Record");
            confirmDialog.setContentText("Are you sure you want to delete this user record?");

            if (confirmDialog.showAndWait().get() == ButtonType.OK) {
                DatabaseHelperParticipent.delete(selected.getId());
                refreshTable();
                clearFields();
                setDefaultJoinDate();
                showAlert("User record deleted successfully.");
            }
        } else {
            showAlert("Please select a user record to delete.");
        }
    }

    @FXML
    private void handleUpdate() {
        RegistrationModel selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (!validateForm()) return;

            DatabaseHelperParticipent.update(
                    selected.getId(),
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    membershipCombo.getValue(),
                    Date.valueOf(joinDateField.getText())
            );

            refreshTable();
            clearFields();
            setDefaultJoinDate();
            showAlert("User record updated successfully.");
        } else {
            showAlert("Please select a user record to update.");
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        setDefaultJoinDate();
        tableView.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        membershipCombo.getSelectionModel().clearSelection();
        healthField.clear();
        emergencyNameField.clear();
        emergencyPhoneField.clear();
        relationshipField.clear();

        nameError.setText("");
        emailError.setText("");
        phoneError.setText("");
        membershipError.setText("");
        if (joinDateError != null) joinDateError.setText("");
    }

    private void refreshTable() {
        ObservableList<RegistrationModel> data = FXCollections.observableArrayList(DatabaseHelperParticipent.getAllUsers());
        tableView.setItems(data);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Management System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}