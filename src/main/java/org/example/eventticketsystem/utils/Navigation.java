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
import org.example.eventticketsystem.dao.UserDAO;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.services.UserService;

import java.io.IOException;
import java.util.Objects;

public class Navigation implements INavigation {
    // Immutability, scenes are switched, while the primary stage remains constant. Avoids redundant object creation and ensures stability
    private final Stage primaryStage;


    private final DependencyRegistry registry;
    private final AppControllerFactory controllerFactory;

    private User currentUser;

    // Default window properties
    private int windowWidth = 420;
    private int windowHeight = 450;

    // Default window properties
    private static final int DEFAULT_WIDTH = 420;
    private static final int DEFAULT_HEIGHT = 450;

    // Constructor requires a stage for Dependency Injection
    public Navigation(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Opsæt dependencies
        this.registry = DependencyRegistry.getInstance();
        this.registry.register(INavigation.class, this);

        this.controllerFactory = new AppControllerFactory(registry);

        setupPrimaryStage();
    }

    // Set/get current logged-in user
    public void setCurrentUser(User user) {
        System.out.println("Navigation@" + System.identityHashCode(this)
                + ": setCurrentUser(" + user + ")");
        this.currentUser = user;
    }

    @Override
    public User getCurrentUser() {
        System.out.println("Navigation@" + System.identityHashCode(this)
                + ": getCurrentUser() -> " + currentUser);
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
            System.err.println("WARNING: Icon not found!");
        }
    }

    /**
     * Loads a new scene.
     * @param fxmlPath Path to the FXML file.
     */

    @Override
    public void loadScene(String fxmlPath) {
        try {

            System.out.println("Loading scene: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();

            // Dynamically Set Window Size Based on Scene
            switch (fxmlPath) {
                case "/views/LoginView.fxml" -> setWindowSize(420, 450);
                case "/views/ControlPanelView.fxml" -> setWindowSize(1280, 800);  // Wider for admin
                case "/views/DashboardView.fxml" -> setWindowSize(1280, 800);  // Standard size
                case "/views/TicketView.fxml" -> setWindowSize(1024, 720);  // Smaller window for users
                default -> setWindowSize(420, 450);  // Default fallback size
            }

            Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
            // Set transparency at scene level (not stage level)
            scene.setFill(Color.TRANSPARENT);

            // Apply global CSS
            String cssPath = "/css/global-style.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(Objects.requireNonNull(
                        getClass().getResource(cssPath)).toExternalForm());
            } else {
                System.err.println("WARNING: Could not load CSS file: " + cssPath);
            }



            // Apply scene
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            System.out.println("Scene switched successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: Failed to load scene: " + fxmlPath);
        }
    }

    @Override
    public Node loadViewNode(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
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
        }
    }

    @Override
    public void closeApplication() {
        primaryStage.close();
}

}