package controller;

import javafx.beans.property.*;

/**
 * Coach model class representing a coach with all required attributes
 */
public class Coach {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty speciality;
    private final StringProperty timeOfWork;
    private final BooleanProperty disponibility;
    private final BooleanProperty privateLessons;

    /**
     * Constructor with all parameters
     */
    public Coach(int id, String name, String speciality, String timeOfWork,
                 boolean disponibility, boolean privateLessons) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.speciality = new SimpleStringProperty(speciality);
        this.timeOfWork = new SimpleStringProperty(timeOfWork);
        this.disponibility = new SimpleBooleanProperty(disponibility);
        this.privateLessons = new SimpleBooleanProperty(privateLessons);
    }

    /**
     * Default constructor
     */
    public Coach() {
        this(0, "", "", "", true, false);
    }

    // Getters and setters for all properties
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSpeciality() {
        return speciality.get();
    }

    public StringProperty specialityProperty() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality.set(speciality);
    }

    public String getTimeOfWork() {
        return timeOfWork.get();
    }

    public StringProperty timeOfWorkProperty() {
        return timeOfWork;
    }

    public void setTimeOfWork(String timeOfWork) {
        this.timeOfWork.set(timeOfWork);
    }

    public boolean isDisponibility() {
        return disponibility.get();
    }

    public BooleanProperty disponibilityProperty() {
        return disponibility;
    }

    public void setDisponibility(boolean disponibility) {
        this.disponibility.set(disponibility);
    }

    public boolean isPrivateLessons() {
        return privateLessons.get();
    }

    public BooleanProperty privateLessonsProperty() {
        return privateLessons;
    }

    public void setPrivateLessons(boolean privateLessons) {
        this.privateLessons.set(privateLessons);
    }

    @Override
    public String toString() {
        return "Coach{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", speciality='" + getSpeciality() + '\'' +
                ", timeOfWork='" + getTimeOfWork() + '\'' +
                ", disponibility=" + isDisponibility() +
                ", privateLessons=" + isPrivateLessons() +
                '}';
    }
}