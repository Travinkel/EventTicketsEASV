package org.example.eventticketsystem.gui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.dal.models.Role;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Injectable
public class EditUserDialogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditUserDialogController.class);
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
    private ComboBox<String> roleComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    private boolean userUpdated = false;
    private User userToEdit;

    public EditUserDialogController(AdminService adminService) {
        this.adminService = adminService;
    }

    public boolean isUserUpdated() {
        return userUpdated;
    }

    public void setUser(User user) {
        this.userToEdit = user;
    }

    @FXML
    private void initialize() {
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> closeDialog());

        roleComboBox.getItems()
                .setAll(adminService.findAllRoles()
                        .stream()
                        .map(Role::getName)
                        .toList());
    }

    private void handleSave() {
        LOGGER.info("🔍 Attempting to update user: {}", userToEdit.getId());

        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String selectedRole = roleComboBox.getValue();

        if (username.isBlank() || fullName.isBlank() || email.isBlank() || selectedRole == null) {
            DialogUtils.showAlert("Fejl", "Alle felter skal udfyldes.");
            return;
        }


        userToEdit.setUsername(username);
        userToEdit.setName(fullName);
        userToEdit.setEmail(email);
        userToEdit.setPhone(phone);

        boolean updated = adminService.updateUser(userToEdit);
        if (updated) {
            LOGGER.info("✅ Successfully updated user: {}", userToEdit.getId());
        } else {
            LOGGER.error("❌ Failed to update user: {}", userToEdit.getId());
            DialogUtils.showAlert("Fejl", "Kunne ikke opdatere bruger.");
            return;
        }

        // Update role if needed
        List<Role> currentRoles = adminService.getRolesForUser(userToEdit.getId());
        Role newRole = adminService.findRoleByName(selectedRole)
                .orElse(null);
        if (newRole != null && (currentRoles.isEmpty() || !currentRoles.get(0)
                .getName()
                .equals(selectedRole))) {
            LOGGER.info("🔍 Attempting to update role for user: {}", userToEdit.getId());
            currentRoles.forEach(r -> adminService.removeRoleFromUser(userToEdit.getId(), r.getId()));
            adminService.assignRoleToUser(userToEdit.getId(), newRole.getId());
            LOGGER.info("✅ Successfully updated role for user: {}", userToEdit.getId());
        }
        userUpdated = true;
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene()
                .getWindow();
        stage.close();
    }

    public void populateFields() {
        if (userToEdit != null) {
            usernameField.setText(userToEdit.getUsername());
            fullNameField.setText(userToEdit.getName());
            emailField.setText(userToEdit.getEmail());
            phoneField.setText(userToEdit.getPhone());

            List<Role> roles = adminService.getRolesForUser(userToEdit.getId());
            if (!roles.isEmpty()) {
                roleComboBox.setValue(roles.get(0)
                        .getName()); // Assuming 1 role
            }
        }
    }
}

