package org.example.eventticketsystem.gui.coordinator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.EventCoordinatorService;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Injectable
public class EditEventDialogController {
    private static final Logger
            LOGGER =
            LoggerFactory.getLogger(EditEventDialogController.class);
    private final EventCoordinatorService
            coordinatorService;
    @FXML
    private TextField
            txtTitle;
    @FXML
    private TextField
            txtStartTime;
    @FXML
    private TextField
            txtEndTime;
    @FXML
    private TextField
            txtLocation;
    private Event
            event;
    private boolean
            eventUpdated =
            false;

    public EditEventDialogController(EventCoordinatorService coordinatorService) {
        this.coordinatorService =
                coordinatorService;
    }

    public void setEvent(Event event) {
        this.event =
                event;
    }

    public void populateFields() {
        txtTitle.setText(event.getTitle());
        txtStartTime.setText(event.getStartTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        txtEndTime.setText(event.getEndTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        txtLocation.setText(event.getLocationGuidance());
    }

    @FXML
    public void handleSave() {
        String
                title =
                txtTitle.getText();
        String
                startTimeText =
                txtStartTime.getText();
        String
                endTimeText =
                txtEndTime.getText();
        String
                location =
                txtLocation.getText();

        if (title.isBlank() ||
            startTimeText.isBlank() ||
            endTimeText.isBlank() ||
            location.isBlank()) {
            showAlert("Fejl",
                    "Alle felter skal udfyldes.");
            return;
        }

        try {
            LocalDateTime
                    startTime =
                    LocalDateTime.parse(startTimeText,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime
                    endTime =
                    LocalDateTime.parse(endTimeText,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (endTime.isBefore(startTime)) {
                showAlert("Fejl",
                        "Sluttid skal være efter starttid.");
                return;
            }

            event.setTitle(title);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setLocationGuidance(location);

            boolean
                    updated =
                    coordinatorService.updateEvent(event);
            if (updated) {
                eventUpdated =
                        true;
                closeDialog();
            } else {
                showAlert("Fejl",
                        "Kunne ikke opdatere event.");
            }
        } catch (DateTimeParseException e) {
            showAlert("Fejl",
                    "Dato og tid skal være i formatet YYYY-MM-DD HH:MM.");
        }
    }

    private void showAlert(String title,
                           String message) {
        Alert
                alert =
                new Alert(Alert.AlertType.ERROR,
                        message,
                        ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage
                stage =
                (Stage) txtTitle.getScene()
                        .getWindow();
        stage.close();
    }

    @FXML
    public void handleCancel() {
        closeDialog();
    }

    public boolean isEventUpdated() {
        return eventUpdated;
    }
}
