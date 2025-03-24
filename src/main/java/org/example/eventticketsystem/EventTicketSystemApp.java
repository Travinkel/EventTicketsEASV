package org.example.eventticketsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.eventticketsystem.models.Event;
import org.example.eventticketsystem.utils.EventReader;
import org.example.eventticketsystem.utils.INavigation;
import org.example.eventticketsystem.utils.Navigation;

import java.util.ArrayList;
import java.util.Arrays;


public class EventTicketSystemApp extends Application {
    private INavigation navigation;

    @Override
    public void start(Stage primaryStage) throws Exception {
        navigation = new Navigation(primaryStage);
        navigation.loadScene("/views/LoginView.fxml");

        EventReader.allEvents = new ArrayList<>();
        System.out.println("Events: " + Arrays.toString(EventReader.allEvents.toArray()));


        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
