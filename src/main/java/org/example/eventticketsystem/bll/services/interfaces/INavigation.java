package org.example.eventticketsystem.bll.services.interfaces;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.dal.models.User;

public interface INavigation {
    void loadScene(String fxmlPath); // still useful

    void loadSceneFromConfig(String configKey); // NEW

    Node loadViewNode(String fxmlPath);

    void setWindowSize(int width, int height);

    void closeApplication();

    Stage getPrimaryStage();

    SessionContext getSession();

    Callback<Class<?>, Object> getControllerFactory();
}
