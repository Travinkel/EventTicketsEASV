package org.example.eventticketsystem.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Could not load config.properties from classpath");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String loginView() {
        return get("views.login");
    }

    public static String controlPanelView() {
        return get("views.controlPanel");
    }

    public static String adminDashboardView() {
        return get("views.admin.dashboard");
    }

    public static String userManagementView() {
        return get("views.admin.userManagement");
    }

    public static String userFormView() {
        return get("views.admin.userForm");
    }

    public static String adminSettingsView() {
        return get("views.admin.settings");
    }

    public static String coordinatorSettingsView() {
        return get("views.coordinator.settings");
    }

    public static String coordinatorDashboardView() {
        return get("views.coordinator.dashboard");
    }

    public static String ticketManagementView() {
        return get("views.coordinator.ticketManagement");
    }

    public static String ticketDetailsView() {
        return get("views.coordinator.ticketDetails");
    }

    public static String eventManagementView() {
        return get("views.coordinator.eventManagement");
    }

    public static String eventFormView() {
        return get("views.coordinator.eventForm");
    }

    public static String confirmDialogView() {
        return get("views.shared.confirmDialog");
    }

    public static String sidebarView() {
        return get("views.shared.sidebar");
    }

    public static String topNavbarView() {
        return get("views.shared.topNavbar");
    }
}