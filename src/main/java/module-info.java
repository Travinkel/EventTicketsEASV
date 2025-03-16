module org.example.eventticketsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens org.example.eventticketsystem to javafx.fxml;
    exports org.example.eventticketsystem;
    exports org.example.eventticketsystem.controllers;
    opens org.example.eventticketsystem.controllers to javafx.fxml;
}