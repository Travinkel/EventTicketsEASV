package org.example.eventticketsystem.gui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.PasswordUtil;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Injectable
public class CreateUserDialogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserDialogController.class);
    private final AdminService adminService;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    private boolean userCreated = false;

    public CreateUserDialogController(AdminService adminService) {
        this.adminService = adminService;
    }

    @FXML
    private void initialize() {
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> closeDialog());
        roleComboBox.getItems()
                .setAll(adminService.findAllRoles()
                        .stream()
                        .map(r -> r.getName())
                        .toList());
    }

    private void handleSave() {
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String roleName = roleComboBox.getValue();

        LOGGER.info("🔍 Attempting to create user: {}", username);

        if (username.isBlank() || fullName.isBlank() || email.isBlank() || password.isBlank() || roleName == null) {
            showAlert("Fejl", "Alle felter skal udfyldes.");
            return;
        }

        User user = User.builder()
                .username(username)
                .hashedPassword(PasswordUtil.hashPassword(password))
                .name(fullName)
                .email(email)
                .phone(phone)
                .createdAt(LocalDateTime.now())
                .build();

        boolean success = adminService.createUserWithRole(user, roleName);
        if (success) {
            LOGGER.info("✅ Successfully created user: {}", username);
            userCreated = true;
            closeDialog();
        } else {
            LOGGER.error("❌ Failed to create user: {}", username);
            showAlert("Error", "Failed to create user.");
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene()
                .getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isUserCreated() {
        return userCreated;
    }
}
