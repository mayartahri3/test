package controller; // Adjust package as needed (e.g., move to a model package if preferred)

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Represents a payment record in the payment management system.
 * Maps to the 'paiements' table with fields: client_id, nom, date_debut, date_fin, montant, statut.
 */
public class Paiement {
    private final StringProperty clientId = new SimpleStringProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateDebut = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dateFin = new SimpleObjectProperty<>();
    private final DoubleProperty montant = new SimpleDoubleProperty();
    private final StringProperty statut = new SimpleStringProperty();

    /**
     * Constructor for a Paiement object.
     *
     * @param clientId   The ID of the client (maps to client_id in the database).
     * @param nom        The name of the client (maps to nom in the database).
     * @param dateDebut  The start date of the payment period.
     * @param dateFin    The end date of the payment period.
     * @param montant    The payment amount.
     * @param statut     The status of the payment (e.g., "Payé").
     */
    public Paiement(String clientId, String nom, LocalDate dateDebut, LocalDate dateFin, double montant, String statut) {
        this.clientId.set(clientId);
        this.nom.set(nom);
        this.dateDebut.set(dateDebut);
        this.dateFin.set(dateFin);
        this.montant.set(montant);
        this.statut.set(statut);
    }

    // Getters for JavaFX properties (for TableView binding)
    public StringProperty clientIdProperty() {
        return clientId;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public ObjectProperty<LocalDate> dateDebutProperty() {
        return dateDebut;
    }

    public ObjectProperty<LocalDate> dateFinProperty() {
        return dateFin;
    }

    public DoubleProperty montantProperty() {
        return montant;
    }

    public StringProperty statutProperty() {
        return statut;
    }

    // Standard getters (for non-property access)
    public String getClientId() {
        return clientId.get();
    }

    public String getNom() {
        return nom.get();
    }

    public LocalDate getDateDebut() {
        return dateDebut.get();
    }

    public LocalDate getDateFin() {
        return dateFin.get();
    }

    public double getMontant() {
        return montant.get();
    }

    public String getStatut() {
        return statut.get();
    }

    // Setters
    public void setClientId(String clientId) {
        this.clientId.set(clientId);
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut.set(dateDebut);
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin.set(dateFin);
    }

    public void setMontant(double montant) {
        this.montant.set(montant);
    }

    public void setStatut(String statut) {
        this.statut.set(statut);
    }

    /**
     * Validates the payment data to ensure all required fields are present and valid.
     *
     * @return true if the payment is valid, false otherwise.
     */
    public boolean isValid() {
        return clientId.get() != null && !clientId.get().isEmpty() &&
                nom.get() != null && !nom.get().isEmpty() &&
                dateDebut.get() != null &&
                dateFin.get() != null &&
                montant.get() >= 0 &&
                statut.get() != null && !statut.get().isEmpty();
    }

    /**
     * Provides a string representation of the payment for debugging or logging.
     *
     * @return A string describing the payment.
     */
    @Override
    public String toString() {
        return String.format("Paiement{clientId=%s, nom=%s, dateDebut=%s, dateFin=%s, montant=%.2f, statut=%s}",
                clientId.get(), nom.get(), dateDebut.get(), dateFin.get(), montant.get(), statut.get());
    }
}