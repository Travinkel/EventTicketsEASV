package org.example.eventticketsystem.bll.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.example.eventticketsystem.bll.services.interfaces.INavigationService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.di.ControllerFactory;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementation of the INavigationService interface.
 * Handles navigation between scenes, session management, and primary stage setup.
 */
@Service
public class NavigationService implements INavigationService {
    private static final Logger
            logger =
            LoggerFactory.getLogger(NavigationService.class);
    private static Config
            config;
    private final Stage
            primaryStage;
    private final ControllerFactory
            controllerFactory;
    private SessionContext
            session;

    /**
     * Constructs a NavigationService instance.
     *
     * @param primaryStage The primary stage of the application.
     */
    @Inject
    public NavigationService(Stage primaryStage,
                             Config config) {
        this.primaryStage =
                primaryStage;
        this.controllerFactory =
                new ControllerFactory();
        this.config =
                config;
        setupPrimaryStage();
    }

    /**
     * Configures the primary stage with default settings.
     */
    private void setupPrimaryStage() {
        logger.info("🛠 Setting up primary stage...");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Event Ticket System");
        primaryStage.setResizable(true);

        // Set application icon
        var
                iconPath =
                getClass().getResourceAsStream("/images/easv_logo.png");
        if (iconPath !=
            null) {
            this.primaryStage.getIcons()
                    .add(new Image(iconPath));
            logger.info("✅ Application icon loaded successfully.");
        } else {
            logger.warn("⚠️ Application icon not found at /images/easv_logo.png");
        }
    }

    /**
     * Switches to a new scene and updates the session context.
     *
     * @param fxmlPath Path to the FXML file for the new scene.
     * @param session  The session context to be used in the new scene.
     */
    public void switchScene(String fxmlPath,
                            SessionContext session) {
        logger.info("🚀 Switching scene to: {}",
                fxmlPath);
        this.session =
                session;
        loadScene(fxmlPath);
    }

    /**
     * Loads a new scene into the primary stage.
     *
     * @param fxmlPath Path to the FXML file for the scene.
     */
    @Override
    public void loadScene(String fxmlPath) {
        try {
            logger.info("🎯 Loading scene: {}",
                    fxmlPath);

            FXMLLoader
                    loader =
                    new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);

            Parent
                    root =
                    loader.load();

            // Dynamically set window size based on scene
            setWindowSizeBasedOnScene(fxmlPath);

            Scene
                    scene =
                    new Scene(root,
                            primaryStage.getWidth(),
                            primaryStage.getHeight());
            scene.setFill(Color.TRANSPARENT);

            // Apply global CSS
            applyGlobalCSS(scene);

            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            logger.info("🎉 Scene loaded and switched to: {}",
                    fxmlPath);

        } catch (IOException e) {
            logger.error("❌ Failed to load scene: {}",
                    fxmlPath,
                    e);
        }
    }

    /**
     * Loads a scene based on a configuration key.
     *
     * @param configKey The key in the configuration file that maps to the FXML path.
     */
    @Override
    public void loadSceneFromConfig(Config.Key configKey) {
        String
                path =
                config.get(configKey);
        if (path ==
            null ||
            path.isEmpty()) {
            logger.error("Config key not found or empty: {}",
                    configKey);
            return;
        }
        loadScene(path);
    }

    /**
     * Loads a view node without switching the primary stage scene.
     *
     * @param fxmlPath Path to the FXML file for the view.
     * @return The loaded view node.
     */
    @Override
    public Node loadViewNode(String fxmlPath) {
        try {
            logger.info("🔍 Loading view node: {}",
                    fxmlPath);
            FXMLLoader
                    loader =
                    new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);
            Node
                    node =
                    loader.load();
            logger.info("✅ View node loaded successfully: {}",
                    fxmlPath);
            return node;
        } catch (IOException e) {
            logger.error("❌ Failed to load view: {}",
                    fxmlPath,
                    e);
            return new Label("❌ Failed to load view: " +
                             fxmlPath);
        }
    }

    /**
     * Sets the window size of the primary stage.
     *
     * @param width  The width of the window.
     * @param height The height of the window.
     */
    @Override
    public void setWindowSize(int width,
                              int height) {
        if (primaryStage !=
            null) {
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
            primaryStage.centerOnScreen();
            logger.debug("🔍 Window size set to {}x{}",
                    width,
                    height);
        }
    }

    /**
     * Closes the application.
     */
    @Override
    public void closeApplication() {
        logger.info("🛑 Closing application...");
        primaryStage.close();
    }

    /**
     * Retrieves the primary stage of the application.
     *
     * @return The primary stage.
     */
    @Override
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Retrieves the current session context.
     *
     * @return The current session context.
     */
    public SessionContext getSession() {
        return session;
    }

    /**
     * Opens a modal dialog and injects the controller using the ControllerFactory.
     *
     * @param fxmlPath        the path to the FXML file for the dialog
     * @param controllerClass the class of the controller
     * @param initializer     a lambda to configure the controller before showing
     * @param <T>             the controller type
     * @return the controller instance
     */
    public <T> T showDialog(String fxmlPath,
                            Class<T> controllerClass,
                            Consumer<T> initializer) {
        try {
            logger.info("🚪 Opening dialog: {}",
                    fxmlPath);
            logger.debug("Attempting to load FXML file: {}",
                    fxmlPath);
            FXMLLoader
                    loader =
                    new FXMLLoader(getClass().getResource(fxmlPath));
            logger.debug("Loading controller using controller factory");
            loader.setControllerFactory(controllerFactory);

            Parent
                    root =
                    loader.load();

            logger.debug("Loading controller instance");
            T
                    controller =
                    loader.getController();
            if (initializer !=
                null) {
                logger.debug("Initializing controller using lambda");
                initializer.accept(controller);
            }

            logger.debug("Creating new dialog stage");
            Stage
                    dialogStage =
                    new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setScene(new Scene(root));
            dialogStage.centerOnScreen();

            logger.debug("Setting window size");
            dialogStage.setWidth(420);
            dialogStage.setHeight(350);

            logger.debug("Showing dialog");
            dialogStage.showAndWait();

            logger.info("✅ Dialog closed: {}",
                    fxmlPath);
            return controller;
        } catch (IOException e) {
            logger.error("❌ Failed to load dialog: {}",
                    fxmlPath,
                    e);
            return null;
        }
    }

    /**
     * Provides the controller factory for dependency injection.
     *
     * @return The controller factory.
     */
    @Override
    public Callback<Class<?>, Object> getControllerFactory() {
        return controllerFactory;
    }

    /**
     * Dynamically sets the window size based on the FXML path.
     *
     * @param fxmlPath The path to the FXML file.
     */
    private void setWindowSizeBasedOnScene(String fxmlPath) {
        logger.debug("🔍 Determining window size for scene: {}",
                fxmlPath);
        switch (fxmlPath) {
            case "/views/shared/LoginView.fxml" -> setWindowSize(420,
                    450);
            case "/views/admin/AdminView.fxml" -> setWindowSize(1280,
                    800);
            case "/views/coordinator/EventCoordinatorView.fxml" -> setWindowSize(1280,
                    800);
            default -> setWindowSize(420,
                    450);
        }
        logger.info("✅ Window size adjusted for scene: {}",
                fxmlPath);
    }

    /**
     * Applies the global CSS to the given scene.
     *
     * @param scene The scene to which the CSS will be applied.
     */
    private void applyGlobalCSS(Scene scene) {
        String
                cssPath =
                config.get(Config.Key.GLOBAL_CSS);
        if (cssPath !=
            null &&
            getClass().getResource(cssPath) !=
            null) {
            scene.getStylesheets()
                    .add(Objects.requireNonNull(getClass().getResource(cssPath))
                            .toExternalForm());
            logger.info("✅ Global CSS applied: {}",
                    cssPath);
        } else {
            logger.warn("⚠️ Global CSS not found or invalid: {}",
                    cssPath);
        }
    }
}
