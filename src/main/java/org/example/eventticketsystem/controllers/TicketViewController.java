package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.eventticketsystem.utils.INavigation;
import org.example.eventticketsystem.utils.Navigation;

public class TicketViewController {

    private final INavigation navigation;

    @FXML private ListView<String> ticketListView;
    @FXML private Button printTicketButton;
    @FXML private Button emailTicketButton;
    @FXML private Button logoutButton;

    public TicketViewController(INavigation navigation) {
        this.navigation = navigation;
    }

    @FXML
    public void initialize() {
        // Placeholder Ticket Data
        ticketListView.getItems().addAll(
                "🎟 EASV Party - 12th April 2025",
                "🎟 Wine Tasting - 25th March 2025",
                "🎟 Dog Show - 30th May 2025"
        );
    }

    @FXML
    private void printTicket() {
        System.out.println("🖨 Printing ticket...");
    }

    @FXML
    private void emailTicket() {
        System.out.println("📧 Emailing ticket...");
    }

    @FXML
    private void logout() {
        System.out.println("🚪 Logging out...");
        navigation.loadScene("/views/LoginView.fxml");
    }
}
