package org.example.eventticketsystem;

import javafx.application.Application;

import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;
import org.example.eventticketsystem.utils.Navigation;

import java.util.Objects;

public class EventTicketSystemApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize Navigation (manages the primary stage)
        Navigation.getInstance().initialize(primaryStage);

        // Load the Login View
        Navigation.getInstance().loadScene("/views/LoginView.fxml", 420, 450);

        // Show the primary stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
