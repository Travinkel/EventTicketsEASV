package org.example.eventticketsystem.utils;

import javafx.util.Callback;
import org.example.eventticketsystem.controllers.*;
import org.example.eventticketsystem.services.UserService;

public class AppControllerFactory implements Callback<Class<?>, Object> {
    private final DependencyRegistry registry;

    public AppControllerFactory(final DependencyRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object call(Class<?> controllerClass) {
        try {
            if (controllerClass == LoginController.class) {
                return new LoginController(
                        registry.get(INavigation.class),
                        registry.get(UserService.class)
                );
            }
            if (controllerClass == SidebarController.class) {
                return new SidebarController(
                        registry.get(INavigation.class),
                        registry.get(UserService.class)
                );
            }
            if (controllerClass == TopNavbarController.class) {
                return new TopNavbarController(
                        registry.get(INavigation.class),
                        registry.get(UserService.class)
                );
            }
            if (controllerClass == ControlPanelController.class) {
                return new ControlPanelController(
                        registry.get(INavigation.class),
                        registry.get(UserService.class)
                );
            }
            if (controllerClass == DashboardController.class) {
                return new DashboardController(
                        registry.get(INavigation.class),
                        registry.get(UserService.class)
                );
            }
            if (controllerClass == EventCoordinatorDashboardController.class) {
                return new EventCoordinatorDashboardController(
                        registry.get(INavigation.class),
                        registry.get(UserService.class)
                );
            }
            if (controllerClass == UserManagementController.class) {
                return new UserManagementController(registry.get(UserService.class));
            }

            // Default fallback for no-arg controllers
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to inject: " + controllerClass.getSimpleName(), e);
        }
    }
}
