package org.example.eventticketsystem.controllers;

import javafx.scene.Node;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;

public abstract class AbstractDashboardController extends BaseController implements IDashboardController {

    public AbstractDashboardController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @Override
    public void refreshDashboard() {}
}
