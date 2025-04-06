package org.example.eventticketsystem.gui.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.EventService;
import org.example.eventticketsystem.bll.services.UserService;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.PasswordUtil;

import java.util.List;

public class UserFormDialogController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ListView<String> roleListView;
    @FXML private ListView<Event> eventListView;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private final UserService userService;
    private final EventService eventService;

    private ObservableList<Event> allEvents;
    private User user;
    private boolean confirmed = false;

    public UserFormDialogController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @FXML
    public void initialize() {
        roleListView.getItems().addAll("ADMIN", "EVENT_COORDINATOR");
        roleListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        eventListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Enable event selection only when EVENT_COORDINATOR is selected
        roleListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            boolean isCoordinator = roleListView.getSelectionModel().getSelectedItems().contains("EVENT_COORDINATOR");
            eventListView.setDisable(!isCoordinator);
        });

        confirmButton.setOnAction(e -> {
            if (isValid()) {
                updateUserFromFields();
                confirmed = true;
                close();
            }
        });

        cancelButton.setOnAction(e -> close());
    }

    public void setUser(User user) {
        this.user = user;

        usernameField.setText(user.getUsername());
        passwordField.setText("");
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());

        // Load all roles
        roleListView.getSelectionModel().clearSelection();
        List<String> userRoles = userService.getRolesForUser(user.getId());
        userRoles.forEach(role -> roleListView.getSelectionModel().select(role));

        // Load events
        allEvents = FXCollections.observableArrayList(eventService.getAllEvents());
        eventListView.setItems(allEvents);
        List<Event> assigned = eventService.getEventsByCoordinator(user.getId());
        assigned.forEach(event -> eventListView.getSelectionModel().select(event));
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean setConfirmed(boolean bool) {
        confirmed = bool;
        return confirmed;
    }

    public User getUser() {
        return user;
    }

    public List<String> getSelectedRoles() {
        return roleListView.getSelectionModel().getSelectedItems();
    }

    public List<Event> getSelectedEvents() {
        return eventListView.getSelectionModel().getSelectedItems();
    }

    private boolean isValid() {
        return !usernameField.getText().isBlank()
                && !nameField.getText().isBlank()
                && !emailField.getText().isBlank()
                && !roleListView.getSelectionModel().getSelectedItems().isEmpty();
    }

    private void updateUserFromFields() {
        user.setUsername(usernameField.getText());
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());

        if (!passwordField.getText().isBlank()) {
            user.setHashedPassword(PasswordUtil.hashPassword(passwordField.getText()));
        }
    }

    private void close() {
        ((Stage) confirmButton.getScene().getWindow()).close();
    }
}
