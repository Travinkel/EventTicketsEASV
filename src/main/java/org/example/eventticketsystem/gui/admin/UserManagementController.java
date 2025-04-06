    package org.example.eventticketsystem.gui.admin;

    import javafx.beans.property.ReadOnlyStringWrapper;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.control.*;
    import javafx.scene.layout.VBox;
    import org.example.eventticketsystem.bll.repositories.UserRepository;
    import org.example.eventticketsystem.di.Injectable;
    import org.example.eventticketsystem.gui.BaseController;
    import org.example.eventticketsystem.dal.models.Event;
    import org.example.eventticketsystem.dal.models.User;
    import org.example.eventticketsystem.bll.viewmodels.UserViewModel;
    import org.example.eventticketsystem.utils.Config;
    import org.example.eventticketsystem.utils.INavigation;
    import org.example.eventticketsystem.utils.PasswordUtil;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Map;

    @Injectable
    public class UserManagementController extends BaseController<User> {
        private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementController.class);

        @FXML private TextField usernameField;
        @FXML private TextField nameField;
        @FXML private TextField emailField;
        @FXML private TextField searchField;
        @FXML private PasswordField passwordField;
        @FXML private ComboBox<String> roleComboBox;
        @FXML private Button createUserButton;
        @FXML private Button editUserButton;
        @FXML private Button deleteUserButton;

        @FXML private TableView<UserViewModel> userTable;
        @FXML private TableColumn<UserViewModel, String> colFullName;
        @FXML private TableColumn<UserViewModel, String> colUsername;
        @FXML private TableColumn<UserViewModel, String> colEmail;
        @FXML private TableColumn<UserViewModel, String> colRole;
        @FXML private TableColumn<UserViewModel, String> colAssignedEvents;


        // cached full list
        private List<User> allUsers;

        private final UserRepository userRepository;

        @FXML private VBox mainContent;


        public UserManagementController(INavigation navigation, UserRepository userRepository) {
            super(navigation);
            this.userRepository = userRepository;
        }

        @FXML
        private void initialize() {
            LOGGER.info("✅ UserManagementController initialized");

            colUsername.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getUsername()));
            colFullName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
            colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
            colRole.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getJoinedRoles()));
            colAssignedEvents.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getJoinedEvents()));


            createUserButton.setOnAction(e -> {
                try {
                    openCreateUserDialog();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            editUserButton.setOnAction(e -> {
                try {
                    openEditUserDialog();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            deleteUserButton.setOnAction(e -> handleDeleteUser());

            searchField.setOnAction(e -> filterUserList(searchField.getText()));
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUserList(newVal));

            userTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            loadUsers();
        }



        @FXML
        private void handleDeleteUser() {
            List<User> selectedUsers = userTable.getSelectionModel().getSelectedItems();
            if (selectedUsers.isEmpty()) {
                showAlert("Vælg mindst én bruger at slette.");
                return;
            }

            boolean allDeleted = true;
            for (User user : selectedUsers) {
                boolean deleted = userService.removeUser(user.getId());
                if (!deleted) allDeleted = false;
            }

            loadUsers();

            if (!allDeleted) {
                showAlert("Nogle brugere kunne ikke slettes.");
            }
        }

        private void filterUserList(String query) {
            if (query == null || query.isBlank()) {
                userTable.getItems().setAll(allUsers);
                return;
            }

            String lowerCaseQuery = query.toLowerCase();

            List<User> filtered = allUsers.stream()
                    .filter(user ->
                            user.getUsername().toLowerCase().contains(lowerCaseQuery) ||
                                    user.getName().toLowerCase().contains(lowerCaseQuery) ||
                                    user.getEmail().toLowerCase().contains(lowerCaseQuery)
                    )
                    .toList();

            userTable.getItems().setAll(filtered);
        }


        private void showAlert(String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }


        private void openCreateUserDialog() throws IOException {
            User newUser = new User(0, "", "", "", "", "", LocalDateTime.now());
            openUserDialog(newUser, true);
        }

        private void openEditUserDialog() throws IOException {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openUserDialog(selected, false);
            } else {
                showAlert("Vælg en bruger for at redigere.");
            }
        }

        private void openUserDialog(User user, boolean isNew) throws IOException {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle(isNew ? "Opret Bruger" : "Rediger Bruger");

            FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.get("views.admin.userForm")));
            VBox content = loader.load();
            UserFormDialogController controller = loader.getController();
            controller.setUser(user);
            dialog.getDialogPane().setContent(content);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    controller.setConfirmed(true);
                    return controller.getUser();
                }
                return null;
            });
            dialog.initOwner(userTable.getScene().getWindow());

            dialog.showAndWait().ifPresent(u -> {
                if (controller.isConfirmed()) {
                    if (isNew) {
                        List<String> selectedRoles = controller.getSelectedRoles(); // this comes from the form dialog

                        boolean success = userService.addUserWithRoles(
                                u.getUsername(),
                                PasswordUtil.hashPassword("password123"),
                                u.getName(),
                                u.getEmail(),
                                selectedRoles,
                                u.getPhone() != null ? u.getPhone() : "00000000",
                                LocalDateTime.now()
                        );

                        if (!success) {
                            showAlert("Kunne ikke oprette brugeren.");
                        }
                    } else {
                        userService.updateUser(u);
                    }
                    loadUsers();
                }
            });
        }

        private void loadUsers() {
            List<User> users = userService.getAllUsers();
            Map<Integer, List<String>> userRoles = userService.getAllUserRolesMapped(); // You implement this
            Map<Integer, List<Event>> userEvents = eventService.getAllEventsMappedByCoordinator(); // You implement this

            List<UserViewModel> viewModels = users.stream().map(user -> {
                List<String> roles = userRoles.getOrDefault(user.getId(), List.of());
                List<Event> events = userEvents.getOrDefault(user.getId(), List.of());
                return new UserViewModel(user, roles, events);
            }).toList();

            userTable.getItems().setAll(viewModels);
        }


        @Override
        public String toString() {
            return "UserManagementController{" +
                    "usernameField=" + usernameField +
                    ", nameField=" + nameField +
                    ", emailField=" + emailField +
                    ", searchField=" + searchField +
                    ", passwordField=" + passwordField +
                    ", roleComboBox=" + roleComboBox +
                    ", createUserButton=" + createUserButton +
                    ", editUserButton=" + editUserButton +
                    ", deleteUserButton=" + deleteUserButton +
                    ", userTable=" + userTable +
                    ", colFullName=" + colFullName +
                    ", colUsername=" + colUsername +
                    ", colEmail=" + colEmail +
                    ", colRole=" + colRole +
                    ", allUsers=" + allUsers +
                    ", mainContent=" + mainContent +
                    '}';
        }
    }
