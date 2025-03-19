package org.example.eventticketsystem.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.eventticketsystem.EventTicketSystemApp;
import org.example.eventticketsystem.models.Event;

import java.io.IOException;
import java.util.Objects;

// Singleton Navigation Manager

public class Navigation {

    // Singleton instance
    private static Navigation instance;

    // Store the primary stage reference
    private Stage primaryStage;

    // Default window properties
    private int windowWidth = 420;
    private int windowHeight = 450;

    // A private constructor prevents instantiation, enforces Singleton pattern
    private Navigation() {}

    /**
     * Get the single instance of Navigation
     * @return Navigation instance.
     */

    public static Navigation getInstance() {
        if (instance == null) {
            instance = new Navigation();
        }
        return instance;
    }

    // Handles one-time setup of the primary stage (icons, title, styles)
    public void initialize(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;

            // Set title and styling
            this.primaryStage.setTitle("Event Ticket System");
            this.primaryStage.initStyle(StageStyle.UNDECORATED);

            // Make window background transparent to support rounded corners
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            // Set application icon
            this.primaryStage.getIcons().add(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/easv_logo.png"))));

            // Set default size
            primaryStage.setHeight(windowHeight);
            primaryStage.setWidth(windowWidth);
        }
    }

    /**
     * Loads a new scene and sets it on the primary stage.
     * This should *only* swap scenes, without modifying global stage properties like size and style
     * @param fxmlPath Path to the FXML file.
     * @param width Width of the window.
     * @param height Height of the window.
     */

    public void loadScene(String fxmlPath, int width, int height) {
        if (primaryStage == null) {
            System.out.println("ERROR: Navigation not initialized. Call initialize() first.");
            return;
        }

        try {
            System.out.println("Loading scene: " + fxmlPath);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, windowWidth, windowHeight);

            // Apply global CSS styling
            String cssPath = "/css/global-style.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(Objects.requireNonNull(
                        Navigation.class.getResource(cssPath)).toExternalForm());
            } else {
                System.err.println("WARNING: Could not load CSS file: " + cssPath);
            }

            // Keep transparency setting for sharp corners
            scene.setFill(Color.TRANSPARENT);

            // Set the scene on the primary stage
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();

            System.out.println("Scene switched successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: Failed to load scene: " + fxmlPath);
        }
    }

    public void setWindowSize(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        if (primaryStage != null) {
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
        }
    }

    /**
     * Gets the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}


