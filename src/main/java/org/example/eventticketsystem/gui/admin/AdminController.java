package org.example.eventticketsystem.gui.admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.bll.services.interfaces.INavigationService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.bll.viewmodels.UserComposite;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Injectable
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;
    private final INavigationService navigationService;
    @FXML
    private VBox adminRoot;
    private SessionContext session;
    // === User Management ===
    @FXML
    private TextField searchUserField;
    @FXML
    private ComboBox<String> roleFilterComboBox;
    @FXML
    private Button createUserButton;
    @FXML
    private Button editUserButton;
    @FXML
    private Button deleteUserButton;
    @FXML
    private TableView<UserComposite> userTable;
    @FXML
    private TableColumn<UserComposite, String> colUsername;
    @FXML
    private TableColumn<UserComposite, String> colFullName;
    @FXML
    private TableColumn<UserComposite, String> colEmail;
    @FXML
    private TableColumn<UserComposite, String> colRole;
    @FXML
    private TableColumn<UserComposite, String> colAssignedEvents;
    // === Event Management ===
    @FXML
    private Button assignCoordinatorButton;
    @FXML
    private Button deleteEventButton;
    @FXML
    private TableView<Event> eventTable;
    @FXML
    private TableColumn<Event, String> colEventTitle;
    @FXML
    private TableColumn<Event, String> colEventStart;
    @FXML
    private TableColumn<Event, String> colEventEnd;
    @FXML
    private TableColumn<Event, String> colEventCoordinators;
    private List<User> allUsers;
    private List<Event> allEvents;
    private boolean userCreated = false;
    private boolean userUpdated = false;

    public AdminController(AdminService adminService, INavigationService navigationService, SessionContext session) {
        this.adminService = adminService;
        this.navigationService = navigationService;
        this.session = session;
        LOGGER.debug("🔧 AdminController instantiated with session ID: {}", session.hashCode());
    }

    @FXML
    private void initialize() {
        LOGGER.info("""
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                🚀 Initializing AdminController
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                """);
        setupUserTable();
        setupEventTable();
        setupActions();
        loadData();
        LOGGER.info("✅ AdminController initialization complete");
    }

    private void setupUserTable() {
        LOGGER.debug("🔧 Setting up User Table columns");
        colUsername.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()
                .getUsername()));
        colFullName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()
                .getName()));
        colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()
                .getEmail()));
        colRole.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()
                .getJoinedRoles()));
        colAssignedEvents.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()
                .getJoinedEvents()));
        LOGGER.info("✅ User Table setup complete");
    }

    private void setupEventTable() {
        LOGGER.debug("🔧 Setting up Event Table columns");
        colEventTitle.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue()
                .getTitle()));
        colEventStart.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue()
                .getStartTime()
                .toString()));
        colEventEnd.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue()
                .getEndTime()
                .toString()));
        colEventCoordinators.setCellValueFactory(e -> {
            List<String> names = adminService.getCoordinatorsForEvent(e.getValue()
                            .getId())
                    .stream()
                    .map(User::getName)
                    .collect(Collectors.toList());
            return new ReadOnlyStringWrapper(String.join(", ", names));
        });
        LOGGER.info("✅ Event Table setup complete");
    }

    private void setupActions() {
        LOGGER.debug("🔧 Setting up button actions and bindings");
        createUserButton.setOnAction(e -> handleCreateUser());
        editUserButton.setOnAction(e -> handleEditUser());
        deleteUserButton.setOnAction(e -> handleDeleteUser());
        assignCoordinatorButton.setOnAction(e -> handleAssignCoordinator());
        deleteEventButton.setOnAction(e -> handleDeleteEvent());

        editUserButton.disableProperty()
                .bind(userTable.getSelectionModel()
                        .selectedItemProperty()
                        .isNull());
        deleteUserButton.disableProperty()
                .bind(userTable.getSelectionModel()
                        .selectedItemProperty()
                        .isNull());
        assignCoordinatorButton.disableProperty()
                .bind(userTable.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
                        .or(eventTable.getSelectionModel()
                                .selectedItemProperty()
                                .isNull()));
        deleteEventButton.disableProperty()
                .bind(eventTable.getSelectionModel()
                        .selectedItemProperty()
                        .isNull());

        searchUserField.textProperty()
                .addListener((obs, oldVal, newVal) -> filterUsers(newVal));
        LOGGER.info("✅ Button actions and bindings setup complete");
    }

    private void handleCreateUser() {
        LOGGER.info("🎉 Create User button clicked");

        CreateUserDialogController controller = navigationService.showDialog(
                "/views/admin/CreateUserDialog.fxml",
                CreateUserDialogController.class,
                (CreateUserDialogController c) -> {
                    LOGGER.debug("🔍 Injecting CreateUserDialogController");
                });

        if (controller != null && controller.isUserCreated()) {
            LOGGER.info("🔁 Refreshing users after new user creation");
            loadUsers();
        }
    }

    private void handleEditUser() {
        UserComposite selectedUser = userTable.getSelectionModel()
                .getSelectedItem();
        if (selectedUser != null) {
            LOGGER.info("✏️ Edit User button clicked for user: {}", selectedUser.getUsername());

            EditUserDialogController controller = navigationService.showDialog(
                    "/views/admin/EditUserDialog.fxml",
                    EditUserDialogController.class,
                    c -> {
                        c.setUser(selectedUser.getUser());
                        c.populateFields();
                        LOGGER.debug("🔍 Injecting EditUserDialogController with user: {}", selectedUser.getUsername());
                    });

            if (controller != null && controller.isUserUpdated()) {
                LOGGER.info("🔁 Refreshing users after user update");
                loadUsers();
            }
        }
    }

    private void handleDeleteUser() {
        UserComposite selected = userTable.getSelectionModel()
                .getSelectedItem();
        if (selected != null) {
            ConfirmDeleteUserController controller = navigationService.showDialog(
                    "/views/admin/ConfirmDeleteUserDialog.fxml",
                    ConfirmDeleteUserController.class,
                    c -> c.setUser(selected.getUser())
            );

            if (controller != null && controller.isDeleted()) {
                LOGGER.info("🔁 Refreshing users after deletion");
                loadUsers();
            }
        }
    }

    private void handleAssignCoordinator() {
        AssignCoordinatorDialogController controller = navigationService.showDialog(
                "/views/admin/AssignCoordinatorDialog.fxml",
                AssignCoordinatorDialogController.class,
                c -> LOGGER.debug("🔍 Injecting AssignCoordinatorDialogController"));

        if (controller != null && controller.isCoordinatorAssigned()) {
            LOGGER.info("🔁 Refreshing events after coordinator assignment");
            loadEvents();
        }
    }

    private void handleDeleteEvent() {
        Event selectedEvent = eventTable.getSelectionModel()
                .getSelectedItem();
        if (selectedEvent != null) {
            ConfirmDeleteEventController controller = navigationService.showDialog(
                    "/views/admin/ConfirmDeleteEventDialog.fxml",
                    ConfirmDeleteEventController.class,
                    c -> c.setEvent(selectedEvent)
            );

            if (controller != null && controller.isDeleted()) {
                LOGGER.info("🔁 Refreshing events after deletion");
                loadEvents();
            }
        }
    }

    private void filterUsers(String query) {
        LOGGER.debug("🔍 Filtering users with query: {}", query);
        if (query == null || query.isBlank()) {
            userTable.getItems()
                    .setAll(toUserComposites(allUsers));
            LOGGER.info("✅ User filter cleared");
            return;
        }
        String lc = query.toLowerCase();
        List<User> filtered = allUsers.stream()
                .filter(u -> u.getUsername()
                        .toLowerCase()
                        .contains(lc)
                        || u.getName()
                        .toLowerCase()
                        .contains(lc)
                        || u.getEmail()
                        .toLowerCase()
                        .contains(lc))
                .toList();
        userTable.getItems()
                .setAll(toUserComposites(filtered));
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
        LOGGER.info("🔄 Loading data for users and events");
        loadUsers();
        loadEvents();
        LOGGER.info("✅ Data loading complete");
    }

    private void loadUsers() {
        LOGGER.debug("🔄 Loading all users");
        this.allUsers = adminService.findAllUsers();
        userTable.getItems()
                .setAll(toUserComposites(allUsers));
        LOGGER.info("✅ Users loaded: {}", allUsers.size());
    }

    private void loadEvents() {
        LOGGER.debug("🔄 Loading all events");
        this.allEvents = adminService.findAllEvents();
        eventTable.getItems()
                .setAll(allEvents);
        LOGGER.info("✅ Events loaded: {}", allEvents.size());
    }
}

