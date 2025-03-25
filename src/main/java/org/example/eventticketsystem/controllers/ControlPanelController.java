package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.models.UserRole;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;

import java.io.IOException;


public class ControlPanelController {
    private final INavigation navigation;
    private final UserService userService;

    @FXML private StackPane sidebarPlaceholder;
    @FXML private StackPane topNavbarPlaceholder;
    @FXML private StackPane contentArea;

    public ControlPanelController(INavigation navigation, UserService userService) {
        this.navigation = navigation;
        this.userService = userService;
    }

    /**
     * Initializes the dashboard controller.
     */

    @FXML
    public void initialize() {
        System.out.println("ControlPanelController@" + this.hashCode()
                + "  navigation: " + System.identityHashCode(navigation));
        User user = navigation.getCurrentUser();

        if (user == null) {
            System.err.println("❌ No user found in navigation! Loading login...");
            navigation.loadScene("/views/LoginView.fxml");
            return;
        }
        loadSidebarFxml();
        loadTopNavBarFxml();

        ContentViewUtils.setContentArea(contentArea);

        UserRole role = user.getRole();
        if (role == UserRole.ADMIN) {
            ContentViewUtils.setContent(navigation.loadViewNode("/views/AdminDashboardView.fxml"));
        } else if (role == UserRole.COORDINATOR) {
            ContentViewUtils.setContent(navigation.loadViewNode("/views/CoordinatorDashboard.fxml"));
        }
    }

    private void loadSidebarFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Sidebar.fxml"));
            loader.setControllerFactory(navigation.getControllerFactory());
            // Or your direct reference to AppControllerFactory

            Node sidebar = loader.load();
            sidebarPlaceholder.getChildren().setAll(sidebar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTopNavBarFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TopNavbar.fxml"));
            loader.setControllerFactory(navigation.getControllerFactory());
            Node topNav = loader.load();
            topNavbarPlaceholder.getChildren().setAll(topNav);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
