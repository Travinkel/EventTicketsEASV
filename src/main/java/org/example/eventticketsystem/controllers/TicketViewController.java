package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.eventticketsystem.models.Ticket;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.INavigation;
import org.example.eventticketsystem.utils.Navigation;

public class TicketViewController extends BaseController<Ticket> {

    @FXML private ListView<String> ticketListView;
    @FXML private Button printTicketButton;
    @FXML private Button emailTicketButton;
    @FXML private Button logoutButton;
    @FXML private Label eventLabel;

    public TicketViewController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @FXML
    public void initialize() {
        if (model != null) {
            eventLabel.setText(model.getEvent().getName());
        }
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
