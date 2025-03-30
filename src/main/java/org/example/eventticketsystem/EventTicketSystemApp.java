package org.example.eventticketsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.eventticketsystem.utils.*;


public class EventTicketSystemApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DependencyRegistry registry = DependencyRegistry.getInstance();

        INavigation navigation = new Navigation(primaryStage);
        registry.register(INavigation.class, navigation);

        navigation.loadScene(Config.loginView());
        primaryStage.setTitle("Event Ticket System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
