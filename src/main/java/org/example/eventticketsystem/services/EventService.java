package org.example.eventticketsystem.services;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EventService {

    @FXML private TextField searchField;
    @FXML private Button createUserButton;
    @FXML private Button addNewButton;

    /**
     * Initializes the dashboard controller.
     */
    @FXML
    public void initialize() {
        System.out.println("Admin Dashboard Loaded!");
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
}
