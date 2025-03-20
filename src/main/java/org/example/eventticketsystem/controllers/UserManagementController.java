package org.example.eventticketsystem.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.services.UserService;

public class UserManagementController {
    // TODO: UserManagementController is to be used by both Event Coordinator Dashboard and Admin Dashboard
    private final UserService userService;

    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button addUserButton;
    @FXML private ListView<User> userListView;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private void initialize() {
        System.out.println("✅ User Management Controller Initialized");

        // Set up roles in the ComboBox
        roleComboBox.getItems().addAll("ADMIN", "COORDINATOR", "USER");
        roleComboBox.setValue("USER");

        // Bind user list to ListView
        userListView.setItems(userService.getUsers());

        // Customize the ListView Display
        userListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText((empty || user == null) ? null : user.getUsername());
            }
        });
    }

    @FXML
    private void handleAddUser() {
        String username = usernameField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();


        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        userService.addUser(username, name, email, password, role);
        clearFields();
    }

    private void clearFields() {
        usernameField.clear();
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("USER");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
