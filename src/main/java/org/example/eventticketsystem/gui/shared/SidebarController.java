package org.example.eventticketsystem.gui.shared;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kordamp.ikonli.javafx.FontIcon;


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

        String role = user.getRole().toLowerCase();

        createButton(" Dashboard", this::handleDashboard, MaterialDesignH.HOME_ACCOUNT);

        if (role.equals("admin")) {
            createButton(" Brugere", this::handleUserManagement, MaterialDesignA.ACCOUNT);
        }

        if (role.equals("coordinator")) {
            createButton("Billetter", this::handleTicketManagement, MaterialDesignT.TICKET_ACCOUNT);
            createButton("Arrangementer", this::handleEventManagement, MaterialDesignC.CALENDAR_ACCOUNT);
        }

        if (role.equals("admin")) {
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
