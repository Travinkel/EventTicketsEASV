package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.models.UserRole;
import org.example.eventticketsystem.services.UserService;

public class UserManagementController {
    private final UserService userService;

    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<UserRole> roleComboBox;
    @FXML private Button addUserButton;
    @FXML private ListView<User> userListView;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private void initialize() {
        System.out.println("✅ User Management Controller Initialized");

        setupRoleComboBox();
        bindUserListView();
    }

    private void setupRoleComboBox() {
        roleComboBox.getItems().setAll(UserRole.values());

        // Optionally filter roles for Event Coordinators
        // Example: remove ADMIN option if the current user shouldn't see it
        // roleComboBox.getItems().remove(UserRole.ADMIN);

        roleComboBox.setValue(UserRole.CUSTOMER);
    }

    private void bindUserListView() {
        userListView.setItems(userService.getUsers());

        userListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText((empty || user == null) ? null : formatUser(user));
            }
        });
    }

    private String formatUser(User user) {
        return String.format("%s (%s)", user.getUsername(), user.getRole());
    }

    @FXML
    private void handleAddUser() {
        String username = usernameField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        UserRole role = roleComboBox.getValue();

        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "All fields are required.");
            return;
        }

        User newUser = new User(0, username, name, email, password, role);
        boolean success = userService.addUser(newUser);

        if (success) {
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to add user. Username might already exist.");
        }
    }

    private void clearFields() {
        usernameField.clear();
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue(UserRole.CUSTOMER);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
