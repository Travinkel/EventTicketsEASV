package org.example.eventticketsystem.gui.admin;

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
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button addUserButton;
    @FXML private ListView<User> userListView;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;

    @FXML private VBox mainContent; // already defined in ControlPanelView.fxml


    public UserManagementController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @FXML
    private void initialize() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers();
    }

    public void showUserManagementView() {
        Node userManagementNode = navigation.loadViewNode(Config.userManagementView());
        mainContent.getChildren().setAll(userManagementNode);
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        userTable.getItems().setAll(users);
    }

    @FXML
    private void handleAddUser() {
        String username = usernameField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        boolean success = userService.addUser(username, password, name, email, role);
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete()) {
            boolean success = userService.removeUser(selected.getId());
            if (success) {
                loadUsers();
                showAlert(Alert.AlertType.INFORMATION, "Brugeren blev slettet.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Kunne ikke slette brugeren.");
            }
        }
    }

    private boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Er du sikker på, at du vil slette brugeren?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    private String formatUser(User user) {
        return String.format("%s (%s)", user.getUsername(), user.getRole());
    }

    private void clearFields() {
        usernameField.clear();
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("customer");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
