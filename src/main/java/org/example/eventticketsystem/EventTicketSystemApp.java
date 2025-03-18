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
        Navigation.getInstance().initialize(primaryStage);
        Navigation.getInstance().loadScene("/views/LoginView.fxml", 420, 450);

        // Set EASV Logo as Window Icon
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/easv_logo.png"))));

        primaryStage.show();

        // primaryStage = stage;
        // stage.initStyle(StageStyle.UNDECORATED);
        // stage.setTitle("Event Ticket System");

         }

    public static void main(String[] args) {
        launch();
    }
}
