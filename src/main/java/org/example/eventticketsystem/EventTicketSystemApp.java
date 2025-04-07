package org.example.eventticketsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.eventticketsystem.bll.services.interfaces.INavigation;
import org.example.eventticketsystem.bll.services.NavigationService;
import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.utils.di.InjectionScanner;
import org.example.eventticketsystem.utils.di.Injector;
import org.example.eventticketsystem.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class EventTicketSystemApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTicketSystemApp.class);


    @Override
    public void start(Stage primaryStage) throws SQLException {
        LOGGER.info("Starting Event Ticket System Application...");

        // Initialize core dependencies
        Injector injector = Injector.getInstance();
        injector.register(DBConnection.class, DBConnection.getInstance());

        // Optional: just for console debug
        String hash = PasswordUtil.hashPassword("admin123");
        System.out.println(hash);

        // Create navigation service
        NavigationService navService = new NavigationService(primaryStage);
        Injector.getInstance().register(NavigationService.class, navService);
        injector.register(INavigation.class, navService);

        // Scan for @Injectable classes
        InjectionScanner.scan("org.example.eventticketsystem");

        // Load Login scene without session
        LOGGER.debug("Loading Login view: {}", Config.loginView());
        navService.loadScene(Config.loginView()); // ✅ this is fine

        // Show primary stage
        primaryStage.setTitle("Event Ticket System");
        primaryStage.show();
        LOGGER.info("Primary stage shown.");
    }

    public static void main(String[] args) {
        launch();
    }
}
