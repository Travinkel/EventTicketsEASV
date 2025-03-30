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

    public static int getInt(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(props.getProperty(key));
    }

    // View Path helpers
    public static String loginView() {
        return get("views.login");
    }

    public static String controlPanelView() {
        return get("views.controlPanel");
    }

    public static String adminDashboardView() {
        return get("views.admin.dashboard");
    }

    public static String dashboardView() {
        return get("views.dashboard");
    }

    public static String sidebarView() {
        return get("views.sidebar");
    }

    public static String topNavbarView() {
        return get("views.topNavbar");
    }

    public static String userManagementView() {
        return get("views.userManagement");
    }

    // Future-proof in-case mailgun needs new keys
    public static void validateKeys(String... keys) {
        for (String key : keys) {
            if (!props.containsKey(key)) {
                throw new IllegalArgumentException("Missing config key: " + key);
            }
        }
    }
}