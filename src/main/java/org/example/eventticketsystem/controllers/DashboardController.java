package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.models.UserRole;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.INavigation;
import org.example.eventticketsystem.services.TicketService;
import org.example.eventticketsystem.services.EventService;


import java.io.IOException;


public class DashboardController extends BaseController {

    @FXML
    private Label dashboardTitle;
    @FXML
    private VBox adminSection;
    @FXML
    private VBox coordinatorSection;

    // Admin Stats UI
    @FXML
    private Label usersCountLabel;
    @FXML
    private Label eventsCountLabel;
    @FXML
    private Label ticketsCountLabel;
    @FXML
    private Button refreshStatsButton;

    // If you have extra services for events/tickets, inject them in the constructor:
    private final EventService eventService;    // optional
    private final TicketService ticketService;  // optional


    public DashboardController(INavigation navigation, UserService userService) {
        super(navigation, userService);
        this.eventService = null;
        this.ticketService = null;
    }


    /**
     * Initializes the dashboard controller.
     */

    @FXML
    public void initialize() {
        User user = navigation.getCurrentUser();
        if (user == null) {
            // fallback or hide everything
            adminSection.setVisible(false);
            coordinatorSection.setVisible(false);
            dashboardTitle.setText("No user?!");
            return;
        }

        switch (user.getRole()) {
            case ADMIN -> {
                adminSection.setVisible(true);
                coordinatorSection.setVisible(false);
                dashboardTitle.setText("Admin Oversigt");
                // Initialize stats right away
                loadAdminStats();
            }
            case COORDINATOR -> {
                adminSection.setVisible(false);
                coordinatorSection.setVisible(true);
                dashboardTitle.setText("Arrangement Oversigt");
            }
            default -> {
                adminSection.setVisible(false);
                coordinatorSection.setVisible(false);
                dashboardTitle.setText("Generic Dashboard - You should not see this!");
            }
        }

    }

    @FXML
    private void handleRefreshStats() {
        loadAdminStats();
    }

    /**
     * Example method that loads stats from services and updates admin labels.
     */
    private void loadAdminStats() {
        // If you have an actual userService:
        int totalUsers = userService.getUsers().size();
        usersCountLabel.setText("Users: " + totalUsers);

        // If you have event/ticket services, do similarly:
        // int totalEvents = eventService.getAllEvents().size();
        // eventsCountLabel.setText("Events: " + totalEvents);

        // int totalTickets = ticketService.getAllTickets().size();
        // ticketsCountLabel.setText("Tickets: " + totalTickets);

        // For now, if we don't have real services, just do placeholders:
        eventsCountLabel.setText("Events: 3");
        ticketsCountLabel.setText("Tickets: 10");
    }
}