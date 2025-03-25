package org.example.eventticketsystem.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.models.UserRole;

import java.util.Optional;

public class UserService {
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private int nextUserId = 4;

    public UserService() {
        // Predefined users for testing
        users.add(new User(1, "admin", "Admin User", "admin@example.com", "password", UserRole.ADMIN));
        users.add(new User(2, "coordinator", "Event Coordinator", "coordinator@example.com", "password", UserRole.COORDINATOR));
        users.add(new User(3, "user", "Regular User", "user@example.com", "password", UserRole.CUSTOMER));
    }

    /**
     * Returns an observable list of all users.
     */
    public ObservableList<User> getUsers() {
        return FXCollections.unmodifiableObservableList(users);
    }

    /**
     * Adds a new user to the list.
     */
    public boolean addUser(String username, String name, String email, String password, UserRole role) {
        if (username.isBlank() || name.isBlank() || email.isBlank() || password.isBlank() || role == null) {
            System.err.println("❌ Cannot add user. Missing required fields.");
            return false;
        }

        if (getUserByUsername(username).isPresent()) {
            System.err.println("❌ Username already exists: " + username);
            return false;
        }

        User newUser = new User(nextUserId++, username, name, email, password, role);
        users.add(newUser);
        System.out.println("✅ User added: " + newUser.getUsername());
        return true;
    }

    /**
     * Removes a user by ID.
     */
    public boolean removeUser(int userId) {
        boolean removed = users.removeIf(user -> user.getId() == userId);
        if (removed) {
            System.out.println("❌ User removed: ID " + userId);
        } else {
            System.err.println("⚠ No user found with ID: " + userId);
        }
        return removed;
    }

    /**
     * Finds a user by username.
     */
    public Optional<User> getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    /**
     * Authenticates a user by username and password.
     */
    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password))
                .findFirst();
    }

    /**
     * Edits an existing user.
     */
    public boolean updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            User existing = users.get(i);
            if (existing.getId() == updatedUser.getId()) {
                users.set(i, updatedUser);
                System.out.println("✏️ User updated: " + updatedUser.getUsername());
                return true;
            }
        }
        System.err.println("⚠ User not found: ID " + updatedUser.getId());
        return false;
    }
}
