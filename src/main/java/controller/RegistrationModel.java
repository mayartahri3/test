package controller;

public class RegistrationModel {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String membership;
    private String joinDate;

    public RegistrationModel(int id, String name, String email, String phone, String membership, String joinDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membership = membership;
        this.joinDate = joinDate;
    }

    // Overloaded constructor without joinDate
    public RegistrationModel(int id, String name, String email, String phone, String membership) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membership = membership;
        this.joinDate = null;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMembership() { return membership; }
    public String getJoinDate() { return joinDate; }
}