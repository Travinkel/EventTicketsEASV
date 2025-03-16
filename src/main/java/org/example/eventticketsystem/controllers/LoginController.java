package org.example.eventticketsystem.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.eventticketsystem.utils.Navigation;

public class LoginController {

    @FXML private VBox loginCard;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button closeButton;
    @FXML private Label errorLabel;

    /**
     * Initializes the Login View with a smooth fade-in animation.
     */
    @FXML
    public void initialize() {
        // Debugging: Ensure FXML components are loaded
        System.out.println("LoginController initialized.");

        // Apply a fade-in effect to the login card
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), loginCard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    /**
     * Handles login authentication and redirects users to their appropriate dashboard.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        System.out.println("Login Attempt: " + username + " / " + password);

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("ERROR: Fields cannot be empty.");
            showError("Username and password cannot be empty.");
            return;
        }

        String userRole = authenticateUser(username, password);

        if (userRole == null) {
            System.out.println("ERROR: Invalid credentials.");
            showError("Invalid credentials. Please try again.");
        } else {
            System.out.println("SUCCESS: User authenticated as " + userRole);
            switch (userRole) {
                case "ADMIN" -> {
                    System.out.println("Navigating to AdminDashboard.fxml");
                    Navigation.loadScene("/views/AdminDashboard.fxml", 1200, 800);
                }
                case "COORDINATOR" -> {
                    System.out.println("Navigating to CoordinatorDashboard.fxml");
                    Navigation.loadScene("/views/CoordinatorDashboard.fxml", 1200, 800);
                }
                case "USER" -> {
                    System.out.println("Navigating to TicketView.fxml");
                    Navigation.loadScene("/views/TicketView.fxml", 900, 700);
                }
                default -> {
                    System.out.println("ERROR: Unknown role assigned.");
                    showError("Unknown role assigned. Contact support.");
                }
            }
        }
    }

    /**
     * Simulates authentication logic.
     * TODO: Replace this with a database lookup.
     */
    private String authenticateUser(String username, String password) {
        if ("admin".equals(username) && "password".equals(password)) return "ADMIN";
        if ("coordinator".equals(username) && "password".equals(password)) return "COORDINATOR";
        if ("user".equals(username) && "password".equals(password)) return "USER";
        return null;
    }

    /**
     * Closes the application when the close button is clicked.
     */
    @FXML
    private void closeApp(ActionEvent event) {
        System.out.println("Closing application...");
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays error messages in the UI.
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
}
