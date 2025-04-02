package org.example.eventticketsystem.gui.admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.INavigation;

import java.util.List;
import javafx.scene.Node;

public class UserManagementController extends BaseController<User> {

    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField searchField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button createUserButton;
    @FXML private Button editUserButton;
    @FXML private Button deleteUserButton;

    @FXML private ListView<User> userListView;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;


    private List<User> allUsers; // cached full list

    @FXML private VBox mainContent; // already defined in ControlPanelView.fxml


    public UserManagementController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @FXML
    private void initialize() {
        colUsername.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getUsername()));
        colFullName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        colRole.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getRole()));

        createUserButton.setOnAction(e -> handleCreateUser());
        editUserButton.setOnAction(e -> handleEditUser());
        deleteUserButton.setOnAction(e -> handleDeleteUser());

        searchField.setOnAction(e -> filterUserList(searchField.getText()));
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUserList(newVal));

        loadUsers();
    }

    public void showUserManagementView() {
        Node userManagementNode = navigation.loadViewNode(Config.userManagementView());
        mainContent.getChildren().setAll(userManagementNode);
    }

    private void loadUsers() {
        allUsers = userService.getAllUsers();
        userTable.getItems().setAll(allUsers);
    }

    @FXML
    private void handleCreateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Opret Bruger");
        dialog.setHeaderText("Indtast brugernavn:");

        dialog.showAndWait().ifPresent(username -> {
            if (!userService.usernameExists(username)) {
                userService.addUser(username, "password123", "Fulde Navn", "email@example.com", "EVENT_COORDINATOR");
                loadUsers();
            } else {
                showAlert("Brugernavn eksisterer allerede.");
            }
        });
    }

    private void handleEditUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vælg en bruger at redigere.");
            return;
        }

        selected.setName("Redigeret Navn"); // TODO: Replace with actual modal later
        selected.setEmail("nyemail@example.com");

        userService.updateUser(selected);
        loadUsers();
    }


    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vælg en bruger at slette.");
            return;
        }

        boolean success = userService.removeUser(selected.getId());
        if (success) {
            loadUsers();
        } else {
            showAlert("Kunne ikke slette brugeren.");
        }
    }


    private void clearFields() {
        usernameField.clear();
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("customer");
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
}
