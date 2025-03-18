package org.example.eventticketsystem.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        public void initialize(Stage stage) {
            if (this.primaryStage == null) {
                this.primaryStage = stage;
            }
        }

    /**
     * Loads a new scene and sets it on the primary stage.
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
            Scene scene = new Scene(root, width, height);

            // Apply global CSS styling if needed
            String cssPath = "/css/global-style.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(Objects.requireNonNull(
                        Navigation.class.getResource(cssPath)).toExternalForm());
            } else {
                System.err.println("WARNING: Could not load CSS file: " + cssPath);
            }


            primaryStage.setScene(scene);
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
            primaryStage.centerOnScreen();

            // Enable full transparency to avoid sharp corners behind rounded elements
            scene.setFill(Color.TRANSPARENT);
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            System.out.println("Scene switched successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: Failed to load scene: " + fxmlPath);
        }
    }
}


