package org.example.eventticketsystem.utils;

import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Singleton
@Scope("singleton")
@Injectable
public class Config {
    private final Properties props = new Properties();

    public Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Could not load config.properties from classpath");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public String get(Key key) {
        return props.getProperty(key.getKey());
    }

    public enum Key {
        LOGIN_VIEW("views.login"),
        CONTROL_PANEL_VIEW("views.controlPanel"),
        ADMIN_DASHBOARD_VIEW("views.admin.dashboard"),
        USER_MANAGEMENT_VIEW("views.admin.userManagement"),
        USER_FORM_VIEW("views.admin.userForm"),
        ADMIN_SETTINGS_VIEW("views.admin.settings"),
        COORDINATOR_SETTINGS_VIEW("views.coordinator.settings"),
        COORDINATOR_DASHBOARD_VIEW("views.coordinator.dashboard"),
        TICKET_MANAGEMENT_VIEW("views.coordinator.ticketManagement"),
        TICKET_DETAILS_VIEW("views.coordinator.ticketDetails"),
        EVENT_MANAGEMENT_VIEW("views.coordinator.eventManagement"),
        EVENT_FORM_VIEW("views.coordinator.eventForm"),
        CONFIRM_DIALOG_VIEW("views.shared.confirmDialog"),
        SIDEBAR_VIEW("views.shared.sidebar"),
        TOP_NAVBAR_VIEW("views.shared.topNavbar"),
        DB_URL("db.url"),
        DB_USERNAME("db.username"),
        DB_PASSWORD("db.password"),
        EMAIL_HOST("email.host"),
        EMAIL_PORT("email.port"),
        EMAIL_USERNAME("email.username"),
        EMAIL_PASSWORD("email.password"),
        EMAIL_FROM("email.from"),
        GLOBAL_CSS("global.css");

        private final String key;

        Key(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
