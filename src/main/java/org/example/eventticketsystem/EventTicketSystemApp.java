package org.example.eventticketsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.eventticketsystem.di.Injector;
import org.example.eventticketsystem.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class EventTicketSystemApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(EventTicketSystemApp.class);


    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting Event Ticket System Application...");
        Injector registry = Injector.getInstance();

        INavigation navigation = new Navigation(primaryStage);
        registry.register(INavigation.class, navigation);

        navigation.loadScene(Config.loginView());

        primaryStage.setTitle("Event Ticket System");
        primaryStage.show();
        logger.info("Primary stage shown.");
    }

    public static void main(String[] args) {
        launch();
    }
}
