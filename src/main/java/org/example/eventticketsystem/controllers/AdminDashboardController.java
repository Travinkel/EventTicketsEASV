package org.example.eventticketsystem.controllers;

import javafx.animation.FadeTransition;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class AdminDashboardController {

    @FXML private TextField searchField;
    @FXML private Button createUserButton;
    @FXML private Button addNewButton;
    private JFXPanel logoutButton;

    /**
     * Initializes the dashboard controller.
     */
    @FXML
    public void initialize() {
        System.out.println("Admin Dashboard Loaded!");
        // Smooth fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8));
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    @FXML
    private void navigateToDashboard() {
        System.out.println("Navigating to Dashboard Overview...");
    }

    @FXML
    private void navigateToUserManagement() {
        System.out.println("Navigating to User Management...");
    }

    @FXML
    private void navigateToManageEvents() {
        System.out.println("Navigating to Event Management...");
    }

    @FXML
    private void navigateToSettings() {
        System.out.println("Navigating to Settings...");
    }

    @FXML
    private void handleCreateUser() {
        System.out.println("Create User Clicked!");
        // TODO: Load the Create User Page
    }

    @FXML
    private void handleAddNew() {
        System.out.println("Add New Clicked!");
        // TODO: Load the Add New Page
    }
    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent loginRoot = fxmlLoader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(loginRoot, 420, 500);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/global-style.css")).toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Event Ticket System - Login");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
