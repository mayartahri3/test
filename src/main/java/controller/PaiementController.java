package controller;

import Db.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PaiementController {

    @FXML
    private ComboBox<String> clientIdComboBox;

    @FXML
    private TextField searchNomField;

    @FXML
    private Label nomField;

    @FXML
    private Spinner<Double> montantSpinner;

    @FXML
    private DatePicker dateDebutPicker, dateFinPicker;

    @FXML
    private ComboBox<String> abonnementComboBox;

    @FXML
    private TableView<Paiement> paiementTable;

    @FXML
    private TableColumn<Paiement, String> colClientId;
    @FXML
    private TableColumn<Paiement, String> colNom;
    @FXML
    private TableColumn<Paiement, LocalDate> colDateDebut;
    @FXML
    private TableColumn<Paiement, LocalDate> colDateFin;
    @FXML
    private TableColumn<Paiement, Double> colMontant;
    @FXML
    private TableColumn<Paiement, String> colStatut;

    private final ObservableList<Paiement> paiementList = FXCollections.observableArrayList();
    private final ObservableList<String> clientNames = FXCollections.observableArrayList();

    // Tarifs abonnements (en euros)
    private static final double TARIF_MENSUEL = 30.0;
    private static final double TARIF_TRIMESTRIEL = 80.0;
    private static final double TARIF_ANNUEL = 300.0;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTableColumns();
        setupSearchFunctionality();
        loadPaiements();
        setupEventListeners();
    }

    private void loadClientInfo(String clientId) {
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Impossible de se connecter à la base de données");
                return;
            }

            String sql = "SELECT name FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nomField.setText(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger les informations du marinade");
        }
    }

    private void loadClientIds() {
        ObservableList<String> clientIds = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Impossible de se connecter à la base de données");
                return;
            }

            String sql = "SELECT id FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clientIds.add(rs.getString("id"));
            }

            clientIdComboBox.setItems(clientIds);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger les IDs des clients");
        }
    }

    private void setupComboBoxes() {
        abonnementComboBox.setItems(FXCollections.observableArrayList("Mensuel", "Trimestriel", "Annuel"));
        loadClientIds();
        loadClientNames();

        clientIdComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadClientInfo(newVal);
            }
        });
    }

    private void loadClientNames() {
        clientNames.clear();
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Impossible de se connecter à la base de données");
                return;
            }

            String sql = "SELECT name FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clientNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger les noms des clients");
        }
    }

    private void setupSearchFunctionality() {
        searchNomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                searchClientByName(newValue);
            } else {
                clientIdComboBox.setValue(null);
                nomField.setText("");
            }
        });
    }

    private void searchClientByName(String searchText) {
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Impossible de se connecter à la base de données");
                return;
            }

            String sql = "SELECT id, name FROM users WHERE name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchText + "%"); // Partial match
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String clientId = rs.getString("id");
                String nom = rs.getString("name");

                clientIdComboBox.setValue(clientId);
                nomField.setText(nom);
            } else {
                clientIdComboBox.setValue(null);
                nomField.setText("");
                showAlert(Alert.AlertType.WARNING, "Aucun résultat", "Aucun client trouvé pour: " + searchText);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de recherche", "Impossible de trouver le client");
        }
    }

    private void setupTableColumns() {
        colClientId.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        paiementTable.setItems(paiementList);
    }

    private void setupEventListeners() {
        abonnementComboBox.setOnAction(e -> {
            setDateFinBasedOnAbonnement();
            calculateMontant();
        });

        dateDebutPicker.setOnAction(e -> {
            setDateFinBasedOnAbonnement();
            calculateMontant();
        });
    }

    private void loadPaiements() {
        paiementList.clear();

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Impossible de se connecter à la base de données");
                return;
            }

            String sql = "SELECT * FROM paiements";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String clientId = rs.getString("client_id");
                String nom = rs.getString("nom");
                LocalDate dateDebut = rs.getDate("date_debut").toLocalDate();
                LocalDate dateFin = rs.getDate("date_fin").toLocalDate();
                double montant = rs.getDouble("montant");
                String statut = rs.getString("statut");

                Paiement paiement = new Paiement(clientId, nom, dateDebut, dateFin, montant, statut);
                paiementList.add(paiement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger les paiements");
        }
    }

    private void setDateFinBasedOnAbonnement() {
        String abonnement = abonnementComboBox.getValue();
        LocalDate dateDebut = dateDebutPicker.getValue();

        if (abonnement != null && dateDebut != null) {
            LocalDate dateFin;

            switch (abonnement) {
                case "Mensuel":
                    dateFin = dateDebut.plusMonths(1);
                    break;
                case "Trimestriel":
                    dateFin = dateDebut.plusMonths(3);
                    break;
                case "Annuel":
                    dateFin = dateDebut.plusYears(1);
                    break;
                default:
                    return;
            }

            dateFinPicker.setValue(dateFin);
        }
    }

    private void calculateMontant() {
        String abonnement = abonnementComboBox.getValue();

        if (abonnement != null) {
            double montant;

            switch (abonnement) {
                case "Mensuel":
                    montant = TARIF_MENSUEL;
                    break;
                case "Trimestriel":
                    montant = TARIF_TRIMESTRIEL;
                    break;
                case "Annuel":
                    montant = TARIF_ANNUEL;
                    break;
                default:
                    return;
            }

            montantSpinner.getValueFactory().setValue(montant);
        }
    }

    @FXML
    private void handlePayer() {
        String id = clientIdComboBox.getValue();
        String nom = nomField.getText();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        Double montant = montantSpinner.getValue();
        String statut = "Payé";

        if (id == null || nom.isEmpty() || dateDebut == null || dateFin == null || montant == null) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Impossible de se connecter à la base de données");
                return;
            }

            String sql = "INSERT INTO paiements (client_id, nom, date_debut, date_fin, montant, statut) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.setString(2, nom);
            stmt.setDate(3, java.sql.Date.valueOf(dateDebut));
            stmt.setDate(4, java.sql.Date.valueOf(dateFin));
            stmt.setDouble(5, montant);
            stmt.setString(6, statut);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur BDD", "Échec de l'enregistrement du paiement.");
            return;
        }

        Paiement paiement = new Paiement(id, nom, dateDebut, dateFin, montant, statut);
        paiementList.add(paiement);

        showAlert(Alert.AlertType.INFORMATION, "Paiement effectué", "Client: " + nom + "\nMontant: " + montant);

        clearFields();
    }

    private void clearFields() {
        clientIdComboBox.getSelectionModel().clearSelection();
        nomField.setText("");
        montantSpinner.getValueFactory().setValue(0.0);
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        abonnementComboBox.getSelectionModel().clearSelection();
        searchNomField.clear();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Paiement");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}