package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.INavigation;

public class TopNavbarController {
    private final INavigation navigation;
    private final UserService userService;

    @FXML private Button closeButton;

    public TopNavbarController(INavigation navigation, UserService userService) {
        this.navigation = navigation;
        this.userService = userService;
    }


    @FXML
    private void handleCloseApp() {
        System.out.println("❌ Close button clicked.");
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles Logout Action
     */
    @FXML
    private void handleLogout() {
        System.out.println("🚪 Logging out...");
        navigation.loadScene("/views/LoginView.fxml");
    }
}
