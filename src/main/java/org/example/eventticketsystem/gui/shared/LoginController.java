package org.example.eventticketsystem.gui.shared;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.AuthService;
import org.example.eventticketsystem.bll.services.NavigationService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.di.Injector;

import javafx.event.ActionEvent;

import java.util.Optional;

@Injectable
public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button closeButton;

    private final AuthService authService;
    private final NavigationService navigationService;

    public LoginController(AuthService authService, NavigationService navigationService) {
        this.authService = authService;
        this.navigationService = navigationService;
    }


    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            // Optional: Add UI feedback (e.g., shake input fields or display label)
            System.out.println("❌ Username or password is empty");
            return;
        }

        Optional<SessionContext> sessionOpt = authService.login(username, password);

        if (sessionOpt.isPresent()) {
            SessionContext session = sessionOpt.get();
            Injector.getInstance().register(SessionContext.class, session, "session");
            System.out.println("✅ User logged in successfully");

            if (session.isAdmin()) {
                navigationService.switchScene("/views/admin/AdminView.fxml", session);
            } else if (session.isEventCoordinator()) {
                navigationService.switchScene("/views/coordinator/EventCoordinatorView.fxml", session);
            } else {
                System.out.println("❌ User has no access role");
                // Optional: show dialog
            }
        } else {
            System.out.println("❌ Invalid credentials");
            // Optional: show dialog
        }
    }

    @FXML
    private void closeApp(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
