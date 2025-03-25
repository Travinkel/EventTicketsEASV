package org.example.eventticketsystem.controllers;

import javafx.scene.Node;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;

public abstract class BaseController {
    protected INavigation navigation;
    protected UserService userService;

    public BaseController(INavigation navigation, UserService userService) {
        this.navigation = navigation;
        this.userService = userService;
    }

    // Common method to load views
    protected void loadView(String viewPath) {
        Node view = navigation.loadViewNode(viewPath);
        ContentViewUtils.setContent(view);
    }
}
