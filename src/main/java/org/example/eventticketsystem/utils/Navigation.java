package org.example.eventticketsystem.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.eventticketsystem.controllers.AdminDashboardController;
import org.example.eventticketsystem.controllers.EventCoordinatorDashboardController;
import org.example.eventticketsystem.controllers.LoginController;
import org.example.eventticketsystem.controllers.UserManagementController;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.services.UserService;

import java.io.IOException;
import java.util.Objects;

public class Navigation implements INavigation {
    // Immutability, scenes are switched, while the primary stage remains constant. Avoids redundant object creation and ensures stability
    private final Stage primaryStage;
    private final UserService userService;

    // Default window properties
    private int windowWidth = 420;
    private int windowHeight = 450;

    // Default window properties
    private static final int DEFAULT_WIDTH = 420;
    private static final int DEFAULT_HEIGHT = 450;

    // Constructor requires a stage for Dependency Injection
    public Navigation(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userService = new UserService();
        setupPrimaryStage();
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

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Inject Navigation into controllers
            fxmlLoader.setControllerFactory(param -> {
                try {
                    if (param.equals(LoginController.class)) {
                        return new LoginController(this, userService); // Inject Navigation
                    } else if (param.equals(AdminDashboardController.class) ||
                            param.equals(EventCoordinatorDashboardController.class)) {
                        return param.getConstructor(INavigation.class, UserService.class)
                                .newInstance(this, userService); // Inject Navigation + UserService
                    } else if (param.equals(UserManagementController.class)) {
                        return param.getConstructor(UserService.class).newInstance(userService); // Inject UserService
                    }
                    return param.getDeclaredConstructor().newInstance(); // Default constructor
                } catch (NoSuchMethodException e) {
                    System.out.println("⚠ No matching constructor for " + param.getName() + ", using default.");
                    try {
                        return param.getDeclaredConstructor().newInstance();
                    } catch (Exception ex) {
                        throw new RuntimeException("❌ Controller Injection Failed: " + param.getName(), ex);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("❌ Controller Injection Failed: " + param.getName(), e);
                }
            });

            Parent root = fxmlLoader.load();

            // Dynamically Set Window Size Based on Scene
            switch (fxmlPath) {
                case "/views/LoginView.fxml" -> setWindowSize(420, 450);
                case "/views/AdminDashboard.fxml" -> setWindowSize(1280, 800);  // Wider for admin
                case "/views/CoordinatorDashboard.fxml" -> setWindowSize(1280, 800);  // Standard size
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


