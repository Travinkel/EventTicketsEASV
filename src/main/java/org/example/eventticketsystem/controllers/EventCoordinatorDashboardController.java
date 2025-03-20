package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.INavigation;

public class EventCoordinatorDashboardController {
    private final INavigation navigation;
    private final UserService userService;

    @FXML private Button sendEmailButton;

    @FXML private TextField searchField;
    @FXML private Button createUserButton;
    @FXML private Button addNewButton;

    public EventCoordinatorDashboardController(INavigation navigation, UserService userService) {
        this.navigation = navigation;
        this.userService = userService;
    }

    /**
     * Initializes the dashboard controller.
     */
    @FXML
    public void initialize() {
        System.out.println("✅ Event Coordinator Dashboard Loaded!");
    }

    @FXML
    private void handleManageEvents() {
        System.out.println("📅 Managing Events...");
        navigation.loadScene("/views/ManageEvents.fxml");
    }

    @FXML
    private void handleNotifyUsers() {
        System.out.println("📢 Sending notifications to users...");
        userService.getUsers().forEach(user ->
                System.out.println("📨 Sending email to: " + user.getEmail())
        );
    }
}
