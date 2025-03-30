package org.example.eventticketsystem.utils;

import javafx.util.Callback;
import org.example.eventticketsystem.gui.*;
import org.example.eventticketsystem.bll.UserService;

public class AppControllerFactory implements Callback<Class<?>, Object> {

    private final DependencyRegistry registry;

    public AppControllerFactory(final DependencyRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object call(Class<?> param) {
        INavigation navigation = registry.get(INavigation.class);
        UserService userService = registry.get(UserService.class);

        if (param == LoginController.class) {
            return new LoginController(navigation, userService);
        } else if (param == DashboardController.class) {
            return new DashboardController(navigation, userService);
        } else if (param == UserManagementController.class) {
            return new UserManagementController(navigation, userService);
        } else if (param == ControlPanelController.class) {
            return new ControlPanelController(navigation, userService);
        } else if (param == SidebarController.class) {
            return new SidebarController(navigation, userService);
        } else if (param == TopNavbarController.class) {
            return new TopNavbarController(navigation, userService);
        }

        // If no match, fallback:
        try {
            return param.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create controller: " + param.getName(), e);
        }
    }
}
