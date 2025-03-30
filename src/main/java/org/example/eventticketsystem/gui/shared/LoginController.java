package org.example.eventticketsystem.gui.shared;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.INavigation;

import java.util.Optional;

public class LoginController extends BaseController<User> {


    @FXML private VBox loginCard;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button closeButton;
    @FXML private Label errorLabel;

    public LoginController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    /**
     * Initializes the Login View with a smooth fade-in animation.
     */
    @FXML
    public void initialize() {
        System.out.println("✅ LoginController initialized");
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

        Optional<User> user = userService.authenticate(username, password);
        if (user.isPresent()) {
            navigation.setCurrentUser(user.get());
            navigation.loadScene(Config.controlPanelView());

        } else {
        showError("Login failed! Invalid username or password.");
        }
    }

    @FXML
    private void closeApp() {
        navigation.closeApplication();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Loginfejl");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
