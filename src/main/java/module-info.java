module org.example.eventticketsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.sql;

    opens org.example.eventticketsystem to javafx.fxml;
    opens org.example.eventticketsystem.controllers to javafx.fxml;
    opens org.example.eventticketsystem.utils to javafx.fxml;

    exports org.example.eventticketsystem;
    exports org.example.eventticketsystem.controllers;
    exports org.example.eventticketsystem.utils;
    exports org.example.eventticketsystem.services;
    exports org.example.eventticketsystem.models;
}
