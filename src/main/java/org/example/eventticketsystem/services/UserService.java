package org.example.eventticketsystem.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.eventticketsystem.models.User;

import java.util.Map;

public class UserService {
    private ObservableList<User> users = FXCollections.observableArrayList();
    private int nextUserId = 4;

    public UserService() {
        users.add(new User(1, "admin", "Admin User", "admin@example.com", "password", "ADMIN"));
        users.add(new User(2, "coordinator", "Event Coordinator", "coordinator@example.com", "password", "COORDINATOR"));
        users.add(new User(3, "user", "Regular User", "user@example.com", "password", "USER"));
    }

    /**
     * Returns an observable list of users (auto-updates UI).
     */
    public ObservableList<User> getUsers() {
        return users;
    }

    /**
     * Adds a new user if the username is unique.
     */
    public void addUser(String username, String name, String email, String password, String role) {
        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            System.err.println("ERROR: Missing user information.");
            return;
        }

        if (getUserByUsername(username) != null) {
            System.err.println("ERROR: Username is already in use.");
            return;
        }

        User newUser = new User(nextUserId++, username, name, email, password, role);
        users.add(newUser);
        System.out.println("✅ User " + newUser + " added to the list.");
    }

    /**
     * Removes a user by ID
     */
    public void removeUser(int userId) {
        boolean removed = users.removeIf(user -> user.getId() == userId);
        if (removed) {
            System.out.println("❌ User removed: ID " + userId);
        } else {
            System.err.println("⚠ ERROR: No user found with ID " + userId);
        }

    }

    public User getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
