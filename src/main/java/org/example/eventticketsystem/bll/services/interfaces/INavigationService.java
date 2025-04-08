package org.example.eventticketsystem.bll.services.interfaces;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.utils.Config;

import java.util.function.Consumer;

public interface INavigationService {
    void loadScene(String fxmlPath); // still useful

    void loadSceneFromConfig(Config.Key configKey); // NEW

    Node loadViewNode(String fxmlPath);

    void setWindowSize(int width, int height);

    void closeApplication();

    Stage getPrimaryStage();

    SessionContext getSession();

    <T> T showDialog(String fxmlPath, Class<T> controllerClass, Consumer<T> initializer);

    Callback<Class<?>, Object> getControllerFactory();
}
