package org.example.eventticketsystem.gui.shared;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;

public class SidebarController extends BaseController<User> {
    @FXML private Label nameLabel;
    @FXML private VBox sidebarContainer;
    @FXML private VBox buttonContainer;

    public SidebarController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @FXML
    public void initialize() {
        System.out.println("SidebarController initialize() called!");

        User user = this.navigation.getCurrentUser();
        if (user == null) return;

        nameLabel.setText(user.getName());

        String role = user.getRole().toLowerCase();

        createButton("🏠 Dashboard", this::handleDashboard);

        if (role.equals("admin")) {
            createButton("👥 Brugere", this::handleUserManagement);
        }

        if (role.equals("coordinator")) {
            createButton("🎟️ Billetter", this::handleTicketManagement);
            createButton("📅 Arrangementer", this::handleEventManagement);
        }

        if (role.equals("admin")) {
            createButton("⚙️ Admin Indstillinger", this::handleAdminSettings);
        } else {
            createButton("⚙️ Mine Indstillinger", this::handleCoordinatorSettings);
        }
    }

    private void createButton(String label, Runnable action) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("sidebar-button");
        btn.setOnAction(e -> action.run());
        buttonContainer.getChildren().add(btn);
    }

    private void handleDashboard() {
        User user = this.navigation.getCurrentUser();
        if (user == null) return;

        String role = user.getRole().toLowerCase();


        String path = switch (role) {
            case "admin" -> Config.adminDashboardView();
            case "event_coordinator" -> Config.coordinatorDashboardView();
            default -> null;
        };

        if (path != null) {
            ContentViewUtils.setContent(navigation.loadViewNode(path));
        }
    }

    private void handleUserManagement() {
        ContentViewUtils.setContent(navigation.loadViewNode(Config.userManagementView()));
    }

    private void handleTicketManagement() {
        ContentViewUtils.setContent(navigation.loadViewNode(Config.ticketManagementView()));
    }

    private void handleEventManagement() {
        ContentViewUtils.setContent(navigation.loadViewNode(Config.eventManagementView()));
    }

    private void handleAdminSettings() {
        ContentViewUtils.setContent(navigation.loadViewNode(Config.adminSettingsView()));
    }

    private void handleCoordinatorSettings() {
        ContentViewUtils.setContent(navigation.loadViewNode(Config.coordinatorSettingsView()));
    }
}
