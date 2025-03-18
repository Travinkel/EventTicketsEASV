package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.eventticketsystem.utils.Navigation;

public class TicketViewController {

    @FXML private ListView<String> ticketListView;
    @FXML private Button printTicketButton;
    @FXML private Button emailTicketButton;
    @FXML private Button logoutButton;

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
        Navigation.getInstance().loadScene("/views/LoginView.fxml", 420, 450);
    }
}
