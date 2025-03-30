package org.example.eventticketsystem.utils;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.eventticketsystem.models.User;

public interface INavigation {
    void loadScene(String fxmlPath); // still useful
    void loadSceneFromConfig(String configKey); // NEW
    Node loadViewNode(String fxmlPath);

    void setWindowSize(int width, int height);
    void closeApplication();

    Stage getPrimaryStage();
    void setCurrentUser(User user);

    User getCurrentUser();
    Callback<Class<?>, Object> getControllerFactory();
}
