package org.example.eventticketsystem.di;

import javafx.util.Callback;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.exceptions.ControllerCreationException;
import org.example.eventticketsystem.gui.admin.AdminDashboardController;
import org.example.eventticketsystem.gui.admin.AdminSettingsController;
import org.example.eventticketsystem.gui.admin.UserManagementController;
import org.example.eventticketsystem.gui.shared.ControlPanelController;
import org.example.eventticketsystem.gui.shared.LoginController;
import org.example.eventticketsystem.gui.shared.SidebarController;
import org.example.eventticketsystem.gui.shared.TopNavbarController;
import org.example.eventticketsystem.utils.INavigation;

import java.lang.reflect.InvocationTargetException;

public class ControllerFactory implements Callback<Class<?>, Object> {

    private final Injector registry;

    public ControllerFactory(final Injector registry) {
        this.registry = registry;
    }

    @Override
    public Object call(Class<?> param) {
        INavigation navigation = registry.get(INavigation.class);
        UserService userService = registry.get(UserService.class);

        return switch (param.getSimpleName()) {
            case "LoginController" -> new LoginController(navigation, userService);
            case "AdminDashboardController" -> new AdminDashboardController(navigation, userService);
            case "UserManagementController" -> new UserManagementController(navigation, userService);
            case "ControlPanelController" -> new ControlPanelController(navigation, userService);
            case "SidebarController" -> new SidebarController(navigation, userService);
            case "TopNavbarController" -> new TopNavbarController(navigation, userService);
            case "AdminSettingsController" -> new AdminSettingsController(navigation, userService);
            default -> createDefault(param);

        };
    }

    private Object createDefault(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new ControllerCreationException("Failed to instantiate controller: " + clazz.getName(), e);
        }
    }

    private <T> T create(Class<T> clazz, INavigation nav, UserService service) {
        try {
            return clazz.getConstructor(INavigation.class, UserService.class).newInstance(nav, service);
        } catch (ReflectiveOperationException e) {
            throw new ControllerCreationException("Failed to create controller: " + clazz.getSimpleName(), e);
        }
    }


    @Override
    public String toString() {
        return "ControllerFactory{" +
                "registry=" + registry +
                '}';
    }
}
