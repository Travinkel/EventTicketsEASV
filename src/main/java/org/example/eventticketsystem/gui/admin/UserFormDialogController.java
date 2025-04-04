package org.example.eventticketsystem.gui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.utils.PasswordUtil;

public class UserFormDialogController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private User user;
    private boolean confirmed = false;

    public void initialize() {
        roleComboBox.getItems().addAll("ADMIN", "EVENT_COORDINATOR");

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
        roleComboBox.setValue(user.getRole());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public User getUser() {
        user.setUsername(usernameField.getText());
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleComboBox.getValue());
        return user;
    }

    private boolean isValid() {
        return !usernameField.getText().isBlank()
                && !nameField.getText().isBlank()
                && !emailField.getText().isBlank()
                && roleComboBox.getValue() != null;
    }

    private void updateUserFromFields() {
        user.setUsername(usernameField.getText());
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleComboBox.getValue());

        if (!passwordField.getText().isBlank()) {
            user.setHashedPassword(PasswordUtil.hashPassword(passwordField.getText()));
        }
    }

    private void close() {
        ((Stage) confirmButton.getScene().getWindow()).close();
    }
}
