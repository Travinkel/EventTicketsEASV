package org.example.eventticketsystem.gui.coordinator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.EventCoordinatorService;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTicketDialogController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateTicketDialogController.class);
    private final EventCoordinatorService coordinatorService;

    @FXML
    private TextField txtUserId;
    @FXML
    private TextField txtPrice;

    private boolean ticketCreated = false;
    private Event event;

    public CreateTicketDialogController(EventCoordinatorService coordinatorService) {
        this.coordinatorService = coordinatorService;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @FXML
    public void handleCreate() {
        String userIdText = txtUserId.getText();
        String priceText = txtPrice.getText();

        if (userIdText.isBlank() || priceText.isBlank()) {
            showAlert("Fejl", "Alle felter skal udfyldes.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdText);
            double price = Double.parseDouble(priceText);

            // Create a new Ticket object
            Ticket ticket = new Ticket();
            ticket.setUserId(userId);
            ticket.setPriceAtPurchase(price);
            ticket.setEventId(event.getId()); // Use eventId from the event object

            boolean created = coordinatorService.createTicket(ticket);
            if (created) {
                ticketCreated = true;
                closeDialog();
            } else {
                showAlert("Fejl", "Kunne ikke oprette billet.");
            }
        } catch (NumberFormatException e) {
            showAlert("Fejl", "Bruger ID og pris skal være gyldige tal.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) txtUserId.getScene()
                .getWindow();
        stage.close();
    }

    @FXML
    public void handleCancel() {
        closeDialog();
    }

    public boolean isTicketCreated() {
        return ticketCreated;
    }
}
