package org.example.eventticketsystem.gui.shared;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.bll.services.UserService;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Injectable
public class ControlPanelController extends BaseController<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlPanelController.class);

    @FXML private StackPane sidebarPlaceholder;
    @FXML private StackPane topNavbarPlaceholder;
    @FXML private StackPane contentArea;

    public ControlPanelController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    /**
     * Initializes the dashboard controller.
     */

    @FXML
    public void initialize() {
        LOGGER.info("✅ ControlPanelController initialized");

        User user = navigation.getCurrentUser();

        if (user == null) {
            System.err.println("❌ No user found in navigation! Loading login...");
            navigation.loadScene(Config.loginView());
            return;
        }

        loadSidebarFxml();
        loadTopNavBarFxml();

        ContentViewUtils.setContentArea(contentArea);
        ContentViewUtils.setContent(navigation.loadViewNode(Config.adminDashboardView()));
    }

    private void loadSidebarFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.sidebarView()));
            loader.setControllerFactory(navigation.getControllerFactory());
            Node sidebar = loader.load();
            sidebarPlaceholder.getChildren().setAll(sidebar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTopNavBarFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.topNavbarView()));
            loader.setControllerFactory(navigation.getControllerFactory());
            Node topNav = loader.load();
            topNavbarPlaceholder.getChildren().setAll(topNav);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
