package org.example.eventticketsystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.models.UserRole;
import org.example.eventticketsystem.models.View;
import org.example.eventticketsystem.services.UserService;
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

        // sidebarContainer.getChildren().clear(); // remove default buttons

        createButton("🏠 Dashboard", this::handleDashboard);
        if (user.getRole() == UserRole.ADMIN) {
            createButton("👥 Brugere", this::handleUserManagement);
        }
        createButton("🎟️ Billetter", this::handleTicketManagement);
        createButton("📅 Arrangementer", this::handleEventManagement);

        if (user.getRole() == UserRole.ADMIN) {
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

        if (user.getRole() == UserRole.ADMIN) {
            ContentViewUtils.setContent(this.navigation.loadViewNode("/views/DashboardView.fxml"));
        } else if (user.getRole() == UserRole.COORDINATOR) {
            ContentViewUtils.setContent(this.navigation.loadViewNode("/views/Dashboard.fxml"));
        }
    }

    private void handleUserManagement() {
        Node view = this.navigation.loadViewNode("/views/UserManagement.fxml");
        ContentViewUtils.setContent(view);
    }

    private void handleTicketManagement() {
        Node view = this.navigation.loadViewNode("/views/TicketManagement.fxml");
        ContentViewUtils.setContent(view);
    }

    private void handleEventManagement() {
        Node view = this.navigation.loadViewNode("/views/EventManagement.fxml");
        ContentViewUtils.setContent(view);
    }

    private void handleAdminSettings() {
        Node view = this.navigation.loadViewNode("/views/AdminSettingsView.fxml");
        ContentViewUtils.setContent(view);
    }

    private void handleCoordinatorSettings() {
        Node view = this.navigation.loadViewNode("/views/CoordinatorSettingsView.fxml");
        ContentViewUtils.setContent(view);
    }

    @Override
    public void loadView() {

    }

    @Override
    public void updateView(Node node) {

    }
}