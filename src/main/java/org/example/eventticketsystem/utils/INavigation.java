package org.example.eventticketsystem.utils;

import javafx.stage.Stage;

public interface INavigation {
    void loadScene(String fxml);
    Stage getPrimaryStage();
    void setWindowSize(int width, int height);
    void closeApplication();
}
