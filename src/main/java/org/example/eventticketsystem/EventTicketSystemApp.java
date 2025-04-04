package org.example.eventticketsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.EmailService;
import org.example.eventticketsystem.bll.EventService;
import org.example.eventticketsystem.bll.TicketPurchaseService;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.dal.EventDAO;
import org.example.eventticketsystem.dal.TicketDAO;
import org.example.eventticketsystem.dal.UserDAO;
import org.example.eventticketsystem.di.InjectionScanner;
import org.example.eventticketsystem.di.Injector;
import org.example.eventticketsystem.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kordamp.ikonli.javafx.FontIcon;

public class EventTicketSystemApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTicketSystemApp.class);


    @Override
    public void start(Stage primaryStage) {
        LOGGER.info("Starting Event Ticket System Application...");
        Injector injector = Injector.getInstance();

        String hash = PasswordUtil.hashPassword("admin123");
        System.out.println(hash);

        INavigation navigation = new Navigation(primaryStage);
        injector.register(INavigation.class, navigation);

        // Scans all @Injectable
        InjectionScanner.scan("org.example.eventticketsystem");

        LOGGER.debug("Loading Login view: {}", Config.loginView());
        navigation.loadScene(Config.loginView());

        primaryStage.setTitle("Event Ticket System");
        primaryStage.show();
        LOGGER.info("Primary stage shown.");
    }

    public static void main(String[] args) {
        launch();
    }
}
