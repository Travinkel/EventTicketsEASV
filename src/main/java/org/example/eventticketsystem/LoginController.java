package org.example.eventticketsystem;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private VBox loginCard;
    @FXML private Label errorLabel;

    // This is called when the FXML is fully loaded
    @FXML
    public void initialize() {
        // Debug
        System.out.println("Login Card: " + loginCard);

        // TODO: Ensure loginCard is not null
        //loginCard.layoutXProperty().bind(loginCard.widthProperty().subtract(loginCard.prefWidthProperty().divide(2)));
        //loginCard.layoutYProperty().bind(loginCard.heightProperty().subtract(loginCard.prefHeightProperty()).divide(2));

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), loginCard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Debug
        System.out.println("LoginController initialized!");
    }


    /**
     * Handles user login when the login button is clicked.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty!");
            return;
        }

        // Simulate authentication system
        if ("admin".equals(username) && "password".equals(password)) {
            loadDashboard();
        } else {
            System.out.println("Invalid username or password!");
        }
        System.out.println("Login attempt: " + username + " / " + password);

        // TODO: Validate user credentials
        // If successful, load next scene (e.g., AdminDashboard)
        // If failed, show error message
    }

    /**
     * Loads the admin dashboard after a successful login.
     */
    public void loadDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/eventticketsystem/adminDashboard.fxml"));
            Parent dashboardRoot = fxmlLoader.load();

            // Get the current scene and replace root
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(dashboardRoot);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Event Ticket System - Admin Dashboard");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeApp(ActionEvent actionEvent) {
    }
}
