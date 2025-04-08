package org.example.eventticketsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.di.InjectionScanner;
import org.example.eventticketsystem.utils.di.Injector;
import org.example.eventticketsystem.bll.services.AppStartupLauncher;

public class EventTicketSystemApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Injector injector = Injector.getInstance();

        // Register singleton instances before scanning
        injector.register(Stage.class, primaryStage);
        injector.register(Config.class, new Config());

        // Scan for all @Injectable, @Service, @Repository
        InjectionScanner.scan("org.example.eventticketsystem");

        // Fully DI-compliant launch
        AppStartupLauncher launcher = injector.get(AppStartupLauncher.class);
        launcher.launchInitialView();

        primaryStage.setTitle("Event Ticket System");
        primaryStage.show();
    }
}
