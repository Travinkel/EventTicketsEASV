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
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Event Ticket System");

        Navigation.loadScene("/views/LoginView.fxml", 420, 450);

        // Set EASV Logo as Window Icon
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/easv_logo.png"))));

        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
