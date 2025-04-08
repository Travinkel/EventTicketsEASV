package org.example.eventticketsystem.gui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Injectable
public class ConfirmDeleteEventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmDeleteEventController.class);
    private final AdminService adminService;
    @FXML
    private Label eventTitleLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    private Event eventToDelete;
    private boolean deleted = false;

    public ConfirmDeleteEventController(AdminService adminService) {
        this.adminService = adminService;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setEvent(Event event) {
        this.eventToDelete = event;
        LOGGER.info("🔍 Event set for deletion: '{}'.", event.getTitle());
        if (eventTitleLabel != null) {
            eventTitleLabel.setText("Titel: " + event.getTitle());
        }
    }

    @FXML
    private void initialize() {
        LOGGER.info("🛠 Initializing ConfirmDeleteEventController...");
        confirmButton.setOnAction(e -> handleConfirm());
        cancelButton.setOnAction(e -> closeDialog());
    }

    private void handleConfirm() {
        LOGGER.info("🔍 Attempting to delete event: '{}'.", eventToDelete.getTitle());
        boolean deleted = adminService.deleteEvent(eventToDelete.getId());
        if (deleted) {
            this.deleted = true;
            LOGGER.info("✅ Successfully deleted event: '{}'.", eventToDelete.getTitle());
        } else {
            LOGGER.error("❌ Failed to delete event: '{}'.", eventToDelete.getTitle());
            showAlert("Fejl", "Kunne ikke slette eventet.");
        }
        closeDialog();
    }

    private void closeDialog() {
        LOGGER.info("🔄 Closing ConfirmDeleteEventDialog...");
        Stage stage = (Stage) confirmButton.getScene()
                .getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        LOGGER.debug("🔍 Showing alert with title: '{}' and message: '{}'.", title, message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
