package org.example.eventticketsystem.controllers;

import javafx.animation.FadeTransition;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.INavigation;

import java.io.IOException;
import java.util.Objects;

public class AdminDashboardController {
    private final INavigation navigation;
    private final UserService userService;

    @FXML private TextField searchField;
    @FXML private Button createUserButton;
    @FXML private Button addEventButton;
    @FXML private Button logoutButton;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;



    public AdminDashboardController(INavigation navigation, UserService userService) {
        this.navigation = navigation;
        this.userService = userService;
    }

    /**
     * Initializes the dashboard controller.
     */

    @FXML
    public void initialize() {
        System.out.println("✅ Admin Dashboard Loaded!");

        // Bind Table Columns to User properties
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Populate TableView with users
        userTable.setItems(userService.getUsers());
    }

    /**
     * Navigate to User Management
     */
    @FXML
    private void handleCreateUser() {
        System.out.println("👤 Create User Clicked!");
        navigation.loadScene("/views/UserManagement.fxml");
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
