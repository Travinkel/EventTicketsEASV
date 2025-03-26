package org.example.eventticketsystem.controllers;

import javafx.scene.Node;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;

public abstract class BaseController<T> {
    protected INavigation navigation;
    protected UserService userService;
    protected T model;

    public BaseController(INavigation navigation, UserService userService) {
        this.navigation = navigation;
        this.userService = userService;
    }

    protected void initializeView() {
        // Child controllers implement this
    }

    public void setModel(T model) {
        this.model = model;
    }

    public T getModel() {
        return model;
    }
}
