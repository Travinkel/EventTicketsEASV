package org.example.eventticketsystem.utils;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.eventticketsystem.models.User;

public interface INavigation {
    void loadScene(String fxml);
    Stage getPrimaryStage();
    void setWindowSize(int width, int height);
    void closeApplication();
    Node loadViewNode(String s);
    void setCurrentUser(User user);
    User getCurrentUser();

    Callback<Class<?>, Object> getControllerFactory();
}
