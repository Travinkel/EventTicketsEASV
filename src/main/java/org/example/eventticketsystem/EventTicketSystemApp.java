package org.example.eventticketsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.eventticketsystem.models.View;
import org.example.eventticketsystem.utils.INavigation;
import org.example.eventticketsystem.utils.Navigation;


public class EventTicketSystemApp extends Application {
    private INavigation navigation;

    @Override
    public void start(Stage primaryStage) throws Exception {
        navigation = new Navigation(primaryStage);
        navigation.loadScene(String.valueOf(View.LOGIN_VIEW));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
