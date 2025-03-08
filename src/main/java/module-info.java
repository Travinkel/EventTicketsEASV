module org.example.eventticketsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.eventticketsystem to javafx.fxml;
    exports org.example.eventticketsystem;
}