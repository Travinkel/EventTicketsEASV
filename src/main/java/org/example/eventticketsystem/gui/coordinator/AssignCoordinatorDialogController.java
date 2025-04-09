package org.example.eventticketsystem.gui.coordinator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Injectable
public class AssignCoordinatorDialogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssignCoordinatorDialogController.class);
    private final AdminService adminService;
    @FXML
    private ComboBox<User> coordinatorComboBox;
    @FXML
    private ComboBox<Event> eventComboBox;
    @FXML
    private Button assignButton;
    @FXML
    private Button cancelButton;
    private boolean coordinatorAssigned = false;
    private Event event;

    public AssignCoordinatorDialogController(AdminService adminService) {
        this.adminService = adminService;
    }

    public boolean isCoordinatorAssigned() {
        return coordinatorAssigned;
    }

    @FXML
    private void initialize() {
        LOGGER.info("🛠 Initializing AssignCoordinatorDialogController...");
        List<User> coordinators = adminService.findAllUsersWithRole("COORDINATOR");
        LOGGER.info("✅ Loaded {} coordinators.", coordinators.size());
        List<Event> events = adminService.findAllEvents();
        LOGGER.info("✅ Loaded {} events.", events.size());

        coordinatorComboBox.getItems()
                .setAll(coordinators);
        eventComboBox.getItems()
                .setAll(events);

        coordinatorComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getName() + " (" + user.getUsername() + ")");
            }
        });

        eventComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty || event == null ? null : event.getTitle());
            }
        });

        assignButton.setOnAction(e -> handleAssign());
        cancelButton.setOnAction(e -> closeDialog());
    }

    private void handleAssign() {
        User selectedCoordinator = coordinatorComboBox.getValue();
        Event selectedEvent = eventComboBox.getValue();

        if (selectedCoordinator == null || selectedEvent == null) {
            LOGGER.warn("⚠️ Coordinator or event not selected.");
            showAlert("Fejl", "Både koordinator og event skal vælges.");
            return;
        }

        LOGGER.info("🔍 Attempting to assign coordinator '{}' to event '{}'.",
                selectedCoordinator.getUsername(), selectedEvent.getTitle());
        boolean success = adminService.assignCoordinatorToEvent(selectedCoordinator.getId(), selectedEvent.getId());

        if (success) {
            LOGGER.info("✅ Successfully assigned coordinator '{}' to event '{}'.",
                    selectedCoordinator.getUsername(), selectedEvent.getTitle());
            coordinatorAssigned = true; // <-- this line is key
            closeDialog();
        } else {
            LOGGER.error("❌ Failed to assign coordinator '{}' to event '{}'.",
                    selectedCoordinator.getUsername(), selectedEvent.getTitle());
            showAlert("Fejl", "Kunne ikke tildele koordinator til event.");
        }
    }

    private void closeDialog() {
        LOGGER.info("🔄 Closing AssignCoordinatorDialog...");
        Stage stage = (Stage) assignButton.getScene()
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

    public void setEvent(Event event) {
        this.event = event;
    }
}
