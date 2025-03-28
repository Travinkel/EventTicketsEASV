package org.example.eventticketsystem.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.INavigation;

public class LoginController {
    private final INavigation navigation;
    private final UserService userService;

    @FXML private VBox loginCard;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button closeButton;
    @FXML private Label errorLabel;

    public LoginController(INavigation navigation, UserService userService) {
        this.navigation = navigation;
        this.userService = userService;
    }

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

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty.");
            return;
        }

        userService.authenticate(username, password).ifPresentOrElse(user -> {
            navigation.setCurrentUser(user);

            switch (user.getRole()) {
                case ADMIN, COORDINATOR -> navigation.loadScene("/views/ControlPanelView.fxml");
                default -> showError("Unknown role assigned. Contact support.");
            }
        }, () -> showError("Invalid credentials. Please try again."));
    }

    private void showError(String s) {
    }
    @FXML
    private void closeApp() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
