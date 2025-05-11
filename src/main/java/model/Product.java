package model;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty category;
    private final DoubleProperty price;
    private final IntegerProperty quantity;
    private final StringProperty photo;
    private final BooleanProperty selected;  // For selection in TableView

    public Product(int id, String name, String category, double price, int quantity, String photo) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.photo = new SimpleStringProperty(photo);
        this.selected = new SimpleBooleanProperty(false);

        // Add validation
        validatePrice(price);
        validateQuantity(quantity);
    }

    public Product(String name, String category, double price, int quantity, String photo) {
        this(-1, name, category, price, quantity, photo);
    }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty categoryProperty() { return category; }
    public DoubleProperty priceProperty() { return price; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty photoProperty() { return photo; }
    public BooleanProperty selectedProperty() { return selected; }

    // Standard getters
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getCategory() { return category.get(); }
    public double getPrice() { return price.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getPhoto() { return photo.get(); }
    public boolean isSelected() { return selected.get(); }

    // Standard setters with validation
    public void setId(int id) { this.id.set(id); }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name.set(name.trim());
    }

    public void setCategory(String category) {
        this.category.set(category == null ? "" : category.trim());
    }

    public void setPrice(double price) {
        validatePrice(price);
        this.price.set(price);
    }

    public void setQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity.set(quantity);
    }

    public void setPhoto(String photo) {
        this.photo.set(photo == null ? "" : photo.trim());
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    // Validation methods
    private void validatePrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    // Calculated properties
    public DoubleProperty totalValueProperty() {
        return new SimpleDoubleProperty(getPrice() * getQuantity());
    }

    public double getTotalValue() {
        return getPrice() * getQuantity();
    }

    // Clone method
    public Product clone() {
        return new Product(
                getId(),
                getName(),
                getCategory(),
                getPrice(),
                getQuantity(),
                getPhoto()
        );
    }

    // Equals and hashCode for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getId() == product.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) - $%.2f x %d = $%.2f",
                getName(), getId(), getPrice(), getQuantity(), getTotalValue());
    }
}