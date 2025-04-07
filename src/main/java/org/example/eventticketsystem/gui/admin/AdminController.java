package org.example.eventticketsystem.gui.admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.bll.services.interfaces.IAdminService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.bll.viewmodels.UserComposite;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @FXML private VBox adminRoot;
    private SessionContext session;


    // === User Management ===
    @FXML private TextField searchUserField;
    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private Button createUserButton;
    @FXML private Button editUserButton;
    @FXML private Button deleteUserButton;
    @FXML private TableView<UserComposite> userTable;
    @FXML private TableColumn<UserComposite, String> colUsername;
    @FXML private TableColumn<UserComposite, String> colFullName;
    @FXML private TableColumn<UserComposite, String> colEmail;
    @FXML private TableColumn<UserComposite, String> colRole;
    @FXML private TableColumn<UserComposite, String> colAssignedEvents;

    // === Event Management ===
    @FXML private Button assignCoordinatorButton;
    @FXML private Button deleteEventButton;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> colEventTitle;
    @FXML private TableColumn<Event, String> colEventStart;
    @FXML private TableColumn<Event, String> colEventEnd;
    @FXML private TableColumn<Event, String> colEventCoordinators;

    private final AdminService adminService;
    private List<User> allUsers;
    private List<Event> allEvents;

    public AdminController(AdminService adminService, SessionContext session) {
        this.adminService = adminService;
        this.session = session;
    }
    @FXML
    private void initialize() {
        LOGGER.info("✅ AdminController initialized");
        setupUserTable();
        setupEventTable();
        setupActions();
        loadData();
    }

    private void setupUserTable() {
        colUsername.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getUsername()));
        colFullName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        colRole.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getJoinedRoles()));
        colAssignedEvents.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getJoinedEvents()));
    }

    private void setupEventTable() {
        colEventTitle.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue().getTitle()));
        colEventStart.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue().getStartTime().toString()));
        colEventEnd.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue().getEndTime().toString()));
        colEventCoordinators.setCellValueFactory(e -> {
            List<String> names = adminService.getCoordinatorsForEvent(e.getValue().getId())
                    .stream().map(User::getName).collect(Collectors.toList());
            return new ReadOnlyStringWrapper(String.join(", ", names));
        });
    }

    private void setupActions() {
        createUserButton.setOnAction(e -> handleCreateUser());
        editUserButton.setOnAction(e -> handleEditUser());
        deleteUserButton.setOnAction(e -> handleDeleteUser());
        assignCoordinatorButton.setOnAction(e -> handleAssignCoordinator());
        deleteEventButton.setOnAction(e -> handleDeleteEvent());

        editUserButton.disableProperty().bind(userTable.getSelectionModel().selectedItemProperty().isNull());
        deleteUserButton.disableProperty().bind(userTable.getSelectionModel().selectedItemProperty().isNull());
        assignCoordinatorButton.disableProperty().bind(userTable.getSelectionModel().selectedItemProperty().isNull()
                .or(eventTable.getSelectionModel().selectedItemProperty().isNull()));
        deleteEventButton.disableProperty().bind(eventTable.getSelectionModel().selectedItemProperty().isNull());

        searchUserField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers(newVal));
    }

    private void handleCreateUser() {
        // TODO: Show user creation dialog
        LOGGER.info("Create user clicked");
    }

    private void handleEditUser() {
        // TODO: Show edit user dialog
        LOGGER.info("Edit user clicked");
    }

    private void handleDeleteUser() {
        UserComposite selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            adminService.deleteUser(selected.getUser().getId());
            loadUsers();
        }
    }

    private void handleAssignCoordinator() {
        UserComposite selectedUser = userTable.getSelectionModel().getSelectedItem();
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null && selectedEvent != null) {
            adminService.assignCoordinatorToEvent(selectedEvent.getId(), selectedUser.getUser().getId());
            loadEvents();
        }
    }

    private void handleDeleteEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            adminService.deleteEvent(selectedEvent.getId());
            loadEvents();
        }
    }

    private void filterUsers(String query) {
        if (query == null || query.isBlank()) {
            userTable.getItems().setAll(toUserComposites(allUsers));
            return;
        }
        String lc = query.toLowerCase();
        List<User> filtered = allUsers.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(lc)
                        || u.getName().toLowerCase().contains(lc)
                        || u.getEmail().toLowerCase().contains(lc))
                .toList();
        userTable.getItems().setAll(toUserComposites(filtered));
    }

    private List<UserComposite> toUserComposites(List<User> users) {
        Map<Integer, List<String>> rolesMap = adminService.getAllUserRolesMapped();
        Map<Integer, List<Event>> eventMap = adminService.getAllEventsMappedByCoordinator();
        return users.stream()
                .map(u -> new UserComposite(
                        u,
                        rolesMap.getOrDefault(u.getId(), List.of()),
                        eventMap.getOrDefault(u.getId(), List.of())
                ))
                .toList();
    }

    private void loadData() {
        loadUsers();
        loadEvents();
    }

    private void loadUsers() {
        this.allUsers = adminService.findAllUsers();
        userTable.getItems().setAll(toUserComposites(allUsers));
    }

    private void loadEvents() {
        this.allEvents = adminService.findAllEvents();
        eventTable.getItems().setAll(allEvents);
    }
}
