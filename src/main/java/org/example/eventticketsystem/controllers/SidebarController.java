package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.INavigation;

public class SidebarController {
    private INavigation navigation;
    private UserService userService;

    public SidebarController(INavigation navigation, UserService userService) {
        this.navigation = navigation;
        this.userService = userService;
    }

    @FXML
    private void handleLogout() {
        System.out.println("🚪 Sidebar Logout Clicked!");
        if (navigation != null) {
            navigation.loadScene("/views/LoginView.fxml");
        }
    }
}
