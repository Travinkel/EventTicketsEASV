package org.example.eventticketsystem.gui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.utils.INavigation;
import org.example.eventticketsystem.utils.ContentViewUtils;

public class AdminSettingsController extends BaseController<User> {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changePasswordButton;
    @FXML private Label statusLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private CheckBox emailNotificationCheckbox;
    @FXML private ChoiceBox<String> themeChoiceBox;
    @FXML private Button applyThemeButton;


    public AdminSettingsController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @FXML
    public void initialize() {
        User current = navigation.getCurrentUser();
        if (current != null) {
            usernameLabel.setText("Brugernavn: " + current.getUsername());
            emailLabel.setText("Email: " + current.getEmail());
            roleLabel.setText("Rolle: " + current.getRole());
        }

        // Password change already implemented
        changePasswordButton.setOnAction(e -> handleChangePassword());

        // Theme logic
        themeChoiceBox.getItems().addAll("Lyst", "Mørkt");
        applyThemeButton.setOnAction(e -> {
            String selected = themeChoiceBox.getValue();
            if ("Mørkt".equals(selected)) {
                ContentViewUtils.setTheme("dark-theme.css");
            } else {
                ContentViewUtils.setTheme("light-theme.css");
            }
        });

        // Notification logic
        emailNotificationCheckbox.setSelected(true); // default for now
    }

    private void handleChangePassword() {
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (newPass.isBlank() || confirm.isBlank()) {
            statusLabel.setText("⚠️ Udfyld begge felter.");
            return;
        }

        if (!newPass.equals(confirm)) {
            statusLabel.setText("❌ Adgangskoderne matcher ikke.");
            return;
        }

        try {
            User current = navigation.getCurrentUser();
            userService.updatePassword(current.getId(), newPass);
            statusLabel.setText("✅ Adgangskode opdateret.");
            newPasswordField.clear();
            confirmPasswordField.clear();
        } catch (Exception ex) {
            statusLabel.setText("❌ Fejl under opdatering.");
            ex.printStackTrace();
        }
    }
}
