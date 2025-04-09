package org.example.eventticketsystem.gui.shared;

import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.AuthService;
import org.example.eventticketsystem.bll.services.NavigationService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Optional;

import static org.mockito.Mockito.*;

class LoginControllerTest extends ApplicationTest {

    @Mock
    private AuthService authService;
    @Mock
    private NavigationService navigationService;

    private LoginController loginController;

    @Override
    public void start(Stage stage) throws Exception {
        MockitoAnnotations.openMocks(this);
        loginController = new LoginController(authService, navigationService);

        // Set up the UI for testing
        loginController.getUsernameField()
                .setText("");
        loginController.getPasswordField()
                .setText("");
        loginController.getLoginButton()
                .setText("Login");
        loginController.getCloseButton()
                .setText("✕");

        stage.setScene(new javafx.scene.Scene(
                new javafx.scene.layout.VBox(
                        loginController.getUsernameField(),
                        loginController.getPasswordField(),
                        loginController.getLoginButton(),
                        loginController.getCloseButton()
                )));
        stage.show();
    }

    @Test
    void testHandleLogin_SuccessfulAdminLogin(FxRobot robot) {
        // Arrange
        when(authService.login("admin", "password")).thenReturn(Optional.of(mockAdminSession()));

        // Act
        robot.clickOn(loginController.getUsernameField())
                .write("admin");
        robot.clickOn(loginController.getPasswordField())
                .write("password");
        robot.clickOn(loginController.getLoginButton());

        // Assert
        verify(navigationService).switchScene("/views/admin/AdminView.fxml", mockAdminSession());
    }

    private SessionContext mockAdminSession() {
        SessionContext mockSession = mock(SessionContext.class);
        when(mockSession.isAdmin()).thenReturn(true);
        return mockSession;
    }

    @Test
    void testHandleLogin_InvalidCredentials(FxRobot robot) {
        // Arrange
        when(authService.login("user", "wrongpassword")).thenReturn(Optional.empty());

        // Act
        robot.clickOn(loginController.getUsernameField())
                .write("user");
        robot.clickOn(loginController.getPasswordField())
                .write("wrongpassword");
        robot.clickOn(loginController.getLoginButton());

        // Assert
        verify(navigationService, never()).switchScene(anyString(), any());
    }

    @Test
    void getAuthService() {
    }

    @Test
    void getNavigationService() {
    }

    @Test
    void getUsernameField() {
    }

    @Test
    void getPasswordField() {
    }

    @Test
    void getLoginButton() {
    }

    @Test
    void getCloseButton() {
    }
}
