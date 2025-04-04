package org.example.eventticketsystem.gui.shared;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.utils.INavigation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Injectable
public class TopNavbarController extends BaseController<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopNavbarController.class);

    @FXML private ImageView userAvatar;

    @FXML private Button closeButton;

    public TopNavbarController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

        //LOGGER.info("✅ TopNavbarController initialized");

    @FXML
    public void initialize() {
        // Round the avatar image in controller
        LOGGER.info("✅ TopNavbarController initialized");

        Platform.runLater(() -> {
            Circle clip = new Circle(17.5, 17.5, 17.5);
            userAvatar.setClip(clip);
        });
    }

    @FXML
    private void handleCloseApp() {
        LOGGER.info("❌ Close button clicked.");

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles Logout Action
     */
    @FXML
    private void handleLogout() {
        System.out.println("🚪 Logging out...");
        navigation.loadScene("/views/shared/LoginView.fxml");
    }

    @Override
    public String toString() {
        return "TopNavbarController{" +
                "closeButton=" + closeButton +
                '}';
    }
}
