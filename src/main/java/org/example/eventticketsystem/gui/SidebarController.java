package org.example.eventticketsystem.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;

public class SidebarController extends BaseController implements IContentView {
    @FXML private Label nameLabel;
    @FXML
    private VBox sidebarContainer;
    @FXML VBox buttonContainer;

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

        createButton("🎟️ Billetter", this::handleTicketManagement);
        createButton("📅 Arrangementer", this::handleEventManagement);

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
        ContentViewUtils.setContent(navigation.loadViewNode(Config.dashboardView()));
    }

    private void handleUserManagement() {
        ContentViewUtils.setContent(navigation.loadViewNode(Config.userManagementView()));
    }

    private void handleTicketManagement() {
        ContentViewUtils.setContent(navigation.loadViewNode("/views/TicketManagement.fxml"));
    }

    private void handleEventManagement() {
        ContentViewUtils.setContent(navigation.loadViewNode("/views/EventManagement.fxml"));
    }

    private void handleAdminSettings() {
        ContentViewUtils.setContent(navigation.loadViewNode("/views/admin/AdminSettingsView.fxml"));
    }

    private void handleCoordinatorSettings() {
        ContentViewUtils.setContent(navigation.loadViewNode("/views/coordinator/CoordinatorSettingsView.fxml"));
    }

    @Override
    public void loadView() {

    }

    @Override
    public void updateView(Node node) {

    }
}