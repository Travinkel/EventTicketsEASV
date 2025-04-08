package org.example.eventticketsystem.gui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Injectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Injectable
public class ConfirmDeleteUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmDeleteUserController.class);
    private final AdminService adminService;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    private User userToDelete;
    private boolean deleted = false;

    public ConfirmDeleteUserController(AdminService adminService) {
        this.adminService = adminService;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setUser(User user) {
        this.userToDelete = user;
        if (userNameLabel != null) {
            userNameLabel.setText("Brugernavn: " + user.getUsername());
        }
    }

    @FXML
    private void initialize() {
        confirmButton.setOnAction(e -> handleConfirm());
        cancelButton.setOnAction(e -> closeDialog());
    }

    private void handleConfirm() {
        LOGGER.info("🔍 Attempting to delete user: {}", userToDelete.getId());
        boolean deleted = adminService.deleteUser(userToDelete.getId());
        if (deleted) {
            this.deleted = true;
            LOGGER.info("✅ Successfully deleted user: {}", userToDelete.getId());
        } else {
            LOGGER.error("❌ Failed to delete user: {}", userToDelete.getId());
            showAlert("Fejl", "Kunne ikke slette brugeren.");
        }
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) confirmButton.getScene()
                .getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
