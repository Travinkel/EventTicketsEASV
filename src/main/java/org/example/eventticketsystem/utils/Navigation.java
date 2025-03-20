package org.example.eventticketsystem.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Navigation implements INavigation {
    // Immutability, scenes are switched, while the primary stage remains constant. Avoids redundant object creation and ensures stability
    private final Stage primaryStage;

    // Default window properties
    private int windowWidth = 420;
    private int windowHeight = 450;

    // Default window properties
    private static final int DEFAULT_WIDTH = 420;
    private static final int DEFAULT_HEIGHT = 450;

    // Constructor requires a stage for Dependency Injection
    public Navigation(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setupPrimaryStage();
    }

    // Handles one-time setup of the primary stage (icons, title, styles)
    private void setupPrimaryStage() {

        // Set title and styling
        primaryStage.setTitle("Event Ticket System");
        primaryStage.setResizable(false);


        // Make window background transparent to support rounded corners
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

            // Set application icon, check for null
            var iconPath = getClass().getResourceAsStream("/images/easv_logo.png");
            if (iconPath != null) {
                this.primaryStage.getIcons().add(new Image(iconPath));
            } else {
                System.err.println("WARNING: Icon not found!");
            }

            // Apply default size
        primaryStage.setWidth(DEFAULT_WIDTH);
            primaryStage.setHeight(DEFAULT_HEIGHT);
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
                    return param.getConstructor(INavigation.class).newInstance(this);
                } catch (NoSuchMethodException e) {
                    System.out.println("⚠ Controller has no matching constructor, using default.");
                    try {
                        return param.getDeclaredConstructor().newInstance();
                    } catch (Exception ex) {
                        throw new RuntimeException("Controller Injection Failed: " + param.getName(), ex);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Controller Injection Failed: " + param.getName(), e);
                }
            });

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());


            // Apply global CSS
            String cssPath = "/css/global-style.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(Objects.requireNonNull(
                        getClass().getResource(cssPath)).toExternalForm());
            } else {
                System.err.println("WARNING: Could not load CSS file: " + cssPath);
            }

            // Set transparency at scene level (not stage level)
            scene.setFill(Color.TRANSPARENT);

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
        }
    }

    @Override
public void closeApplication() {
        primaryStage.close();
}

}


