package org.example.eventticketsystem.gui.shared;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.eventticketsystem.bll.services.UserService;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.INavigation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Injectable
public class LoginController extends BaseController<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

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
        LOGGER.info("✅ LoginController initialized");
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
        LOGGER.debug("Attempting login for: {}", username);

        Optional<User> user = userService.authenticate(username, password);

        if (user.isPresent()) {
            LOGGER.info("Login successful for user: {}", user.get().getUsername());

            navigation.setCurrentUser(user.get());
            navigation.loadScene(Config.controlPanelView());

        } else {
            LOGGER.warn("Login failed for username: {}", username);
        }
    }

    @FXML
    private void closeApp() {
        navigation.closeApplication();
    }

    @Override
    public String toString() {
        return "LoginController{" +
                "loginCard=" + loginCard +
                ", usernameField=" + usernameField +
                ", passwordField=" + passwordField +
                ", loginButton=" + loginButton +
                ", closeButton=" + closeButton +
                ", errorLabel=" + errorLabel +
                '}';
    }
}
