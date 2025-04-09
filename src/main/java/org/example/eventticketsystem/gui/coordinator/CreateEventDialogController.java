package org.example.eventticketsystem.gui.coordinator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.EventCoordinatorService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Injectable
public class CreateEventDialogController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateEventDialogController.class);
    private final EventCoordinatorService coordinatorService;
    private final SessionContext session; // 👈 add this
    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtStartTime;
    @FXML
    private TextField txtEndTime;
    @FXML
    private TextField txtLocation;
    private boolean eventCreated = false;
    private Event event;

    @Inject
    public CreateEventDialogController(EventCoordinatorService coordinatorService, SessionContext session) {
        this.coordinatorService = coordinatorService;
        this.session = session;
    }

    @FXML
    public void handleCreate() {
        String title = txtTitle.getText();
        String startTimeText = txtStartTime.getText();
        String endTimeText = txtEndTime.getText();
        String location = txtLocation.getText();

        if (title.isBlank() || startTimeText.isBlank() || endTimeText.isBlank() || location.isBlank()) {
            showAlert("Fejl", "Alle felter skal udfyldes.");
            return;
        }

        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeText,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime endTime = LocalDateTime.parse(endTimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (endTime.isBefore(startTime)) {
                showAlert("Fejl", "Sluttid skal være efter starttid.");
                return;
            }

            Event event = new Event();
            event.setTitle(title);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setLocationGuidance(location);

            int coordinatorId = session.getCurrentUser()
                    .getId();
            boolean created = coordinatorService.createEvent(event, session.getCurrentUser()
                    .getId());
            if (created) {
                eventCreated = true;
                closeDialog();
            } else {
                showAlert("Fejl", "Kunne ikke oprette event.");
            }
        } catch (DateTimeParseException e) {
            showAlert("Fejl", "Dato og tid skal være i formatet YYYY-MM-DD HH:MM.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) txtTitle.getScene()
                .getWindow();
        stage.close();
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @FXML
    public void handleCancel() {
        closeDialog();
    }

    public boolean isEventCreated() {
        return eventCreated;
    }
}
