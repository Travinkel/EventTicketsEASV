package org.example.eventticketsystem;

import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.eventticketsystem.utils.INavigation;
import org.example.eventticketsystem.utils.Navigation;


public class EventTicketSystemApp extends Application {
    private INavigation navigation;

    @Override
    public void start(Stage primaryStage) throws Exception {
        navigation = new Navigation(primaryStage);
        navigation.loadScene("/views/LoginView.fxml");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
