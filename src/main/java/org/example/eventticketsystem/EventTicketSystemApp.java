package org.example.eventticketsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class EventTicketSystemApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML for the login screen
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/eventticketsystem/login.fxml"));
        Objects.requireNonNull(fxmlLoader, "FXML file is missing!");

        // Create a Scene with the loaded FXML
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1440, 900);

        // Use external css for fluent design implementation, consistent styling, easier maintenance
        // Note. toExternalForm() converts it to a format that JavaFX can use
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        // Frameless Window
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // Set the rest of the stage
        primaryStage.setTitle("Event Ticket System - Login");
        primaryStage.setScene(scene);

        // Allow resizing and dynamic adjustment --best practice
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);

        // Maximize the window at startup
        primaryStage.setMaximized(true);

        primaryStage.show();

        // Set EASV Logo as a window icon
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/easv_logo.png"))));
    }

    public static void main(String[] args) {
        launch();
    }
}