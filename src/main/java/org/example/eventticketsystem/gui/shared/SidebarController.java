package org.example.eventticketsystem.gui.shared;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.bll.services.UserService;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;


@Injectable
public class SidebarController extends BaseController<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SidebarController.class);

    @FXML private Label nameLabel;
    @FXML private VBox sidebarContainer;
    @FXML private VBox buttonContainer;

    public SidebarController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @FXML
    public void initialize() {
        LOGGER.info("✅ SidebarController initialized");

        User user = this.navigation.getCurrentUser();
        if (user == null) return;

        nameLabel.setText(user.getName());

        // ✅ Get all roles from DB
        var roles = userService.getRolesForUser(user.getId())
                .stream().map(String::toLowerCase).toList();

        createButton(" Dashboard", this::handleDashboard, MaterialDesignH.HOME_ACCOUNT);

        if (roles.contains("admin")) {
            createButton(" Brugere", this::handleUserManagement, MaterialDesignA.ACCOUNT);
        }

        if (roles.contains("coordinator")) {
            createButton("Billetter", this::handleTicketManagement, MaterialDesignT.TICKET_ACCOUNT);
            createButton("Arrangementer", this::handleEventManagement, MaterialDesignC.CALENDAR_ACCOUNT);
        }

        if (roles.contains("admin")) {
            createButton("Admin Indstillinger", this::handleAdminSettings, MaterialDesignA.ACCOUNT_COG);
        } else {
            createButton("Mine Indstillinger", this::handleCoordinatorSettings, MaterialDesignA.ACCOUNT_COG);
        }
    }

    private void createButton(String label, Runnable action, Ikon ikon) {
        FontIcon fontIcon = new FontIcon(ikon);
        fontIcon.setIconSize(18); // Optional
        fontIcon.getStyleClass().add("white-icon");

        Button btn = new Button(label, fontIcon);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("sidebar-button");
        btn.setOnAction(e -> action.run());
        buttonContainer.getChildren().add(btn);
    }

    private void handleDashboard() {
        User user = this.navigation.getCurrentUser();
        if (user == null) return;

        List<String> roles = userService.getRolesForUser(user.getId())
                .stream().map(String::toLowerCase).toList();

        String path = null;
        if (roles.contains("admin")) {
            path = Config.adminDashboardView();
        } else if (roles.contains("coordinator") || roles.contains("event_coordinator")) {
            path = Config.coordinatorDashboardView();
        }

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
