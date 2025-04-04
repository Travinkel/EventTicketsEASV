package org.example.eventticketsystem.models;

import java.time.LocalDateTime;
import java.util.List;

public class User extends BaseEntity {
    private String username;
    private String hashedPassword;
    private String name;
    private String email;
    private String role;
    private String phone;
    private LocalDateTime createdAt;
    private List<String> globalRoles;
    private List<Event> coordinatedEvents;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User(int id, String username, String hashedPassword, String name, String email, String role, String phone, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    // Getters and setters...

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
