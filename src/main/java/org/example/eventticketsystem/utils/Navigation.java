package org.example.eventticketsystem.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.example.eventticketsystem.di.ControllerFactory;
import org.example.eventticketsystem.di.Injector;
import org.example.eventticketsystem.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class Navigation implements INavigation {
    // Immutability, scenes are switched, while the primary stage remains constant. Avoids redundant object creation and ensures stability
    private final Stage primaryStage;

    private static final Logger logger = LoggerFactory.getLogger(Navigation.class);


    private final Injector registry;
    private final ControllerFactory controllerFactory;

    private User currentUser;

    // Constructor requires a stage for Dependency Injection
    public Navigation(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Opsæt dependencies
        this.registry = Injector.getInstance();
        this.registry.register(INavigation.class, this);

        this.controllerFactory = new ControllerFactory();

        setupPrimaryStage();
    }

    // Set/get current logged-in user
    public void setCurrentUser(User user) {
        this.currentUser = user;
        logger.debug("Set current user: {}", user);
    }

    @Override
    public User getCurrentUser() {
        logger.debug("Getting current user: {}", currentUser);
        return currentUser;
    }

    @Override
    public Callback<Class<?>, Object> getControllerFactory() {
        return controllerFactory;
    }

    // Handles one-time setup of the primary stage (icons, title, styles)
    private void setupPrimaryStage() {
        // Make window background transparent to support rounded corners
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        // Set title and styling
        primaryStage.setTitle("Event Ticket System");
        primaryStage.setResizable(true);

        // Set application icon, check for null
        var iconPath = getClass().getResourceAsStream("/images/easv_logo.png");
        if (iconPath != null) {
            this.primaryStage.getIcons().add(new Image(iconPath));
        } else {
            logger.warn("Application icon not found at /images/easv_logo.png");
        }
    }

    /**
     * Loads a new scene.
     * @param fxmlPath Path to the FXML file.
     */

    @Override
    public void loadScene(String fxmlPath) {
        try {
            logger.info("Loading scene: {}", fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();

            // Dynamically Set Window Size Based on Scene
            switch (fxmlPath) {
                case "/views/shared/LoginView.fxml" -> setWindowSize(420, 450);
                case "/views/shared/ControlPanelView.fxml" -> setWindowSize(1280, 800);  // Wider for admin
                case "/views/admin/AdminDashboardView.fxml" -> setWindowSize(1280, 800);  // Standard size
                case "/views/TicketView.fxml" -> setWindowSize(1024, 720);  // Smaller window for users
                default -> setWindowSize(420, 450);  // Default fallback size
            }

            Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
            // Set transparency at scene level (not stage level)
            scene.setFill(Color.TRANSPARENT);

            // Apply global CSS
            String cssPath = Config.get("global.css");
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(Objects.requireNonNull(
                        getClass().getResource(cssPath)).toExternalForm());
            } else {
                logger.warn("CSS not found at {}", cssPath);
            }



            // Apply scene
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            logger.info("Scene loaded and switched to: {}", fxmlPath);

        } catch (IOException e) {
            logger.error("Failed to load scene: {}", fxmlPath, e);
        }
    }

    @Override
    public void loadSceneFromConfig(String configKey) {
        String path = Config.get(configKey);
        if (path == null || path.isEmpty()) {
            logger.error("Config key not found or empty: {}", configKey);
            return;
        }
        loadScene(path);
    }

    @Override
    public Node loadViewNode(String fxmlPath) {
        try {
            logger.info("Loading view node: {}", fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);
            return loader.load();
        } catch (IOException e) {
            logger.error("Failed to load view: {}", fxmlPath, e);
            return new Label("❌ Failed to load view: " + fxmlPath);
        }
    }

    /**
     * Gets the primary stage.
     */
    @Override
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void setWindowSize(int width, int height) {
        if (primaryStage != null) {
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
            primaryStage.centerOnScreen();
            logger.debug("Window size set to {}x{}", width, height);
        }
    }

    @Override
    public void closeApplication() {
        logger.info("Closing application...");
        primaryStage.close();
}

}