package org.example.eventticketsystem.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.eventticketsystem.EventTicketSystemApp;

import java.io.IOException;
import java.util.Objects;

public class Navigation {
    /**
     * Loads a new scene and sets it on the primary stage.
     * @param fxmlPath Path to the FXML file.
     * @param width Width of the window.
     * @param height Height of the window.
     */

    public static void loadScene(String fxmlPath, int width, int height) {
        try {
            System.out.println("Loading scene: " + fxmlPath);

            FXMLLoader fxmlLoader = new FXMLLoader(Navigation.class.getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, width, height);

            // Apply global CSS styling if needed
            scene.getStylesheets().add(Objects.requireNonNull(
                    Navigation.class.getResource("/css/global-style.css")).toExternalForm());

            // Get the primary stage from EventTicketSystemApp
            Stage primaryStage = EventTicketSystemApp.getPrimaryStage();
            primaryStage.setScene(scene);
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
            primaryStage.centerOnScreen();

            System.out.println("Scene switched successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: Failed to load scene: " + fxmlPath);
        }
    }
}


