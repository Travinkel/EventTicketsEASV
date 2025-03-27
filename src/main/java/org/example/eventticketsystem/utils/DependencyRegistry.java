package org.example.eventticketsystem.utils;

import org.example.eventticketsystem.dao.UserDAO;
import org.example.eventticketsystem.services.UserService;

import java.util.HashMap;
import java.util.Map;

public class DependencyRegistry {

    private static final DependencyRegistry instance = new DependencyRegistry();

    private final Map<Class<?>, Object> services = new HashMap<>();

    private DependencyRegistry() {
        // Register Shared dependencies here
        UserDAO userDAO = new UserDAO();
        register(UserDAO.class, new UserDAO());
        register(UserService.class, new UserService(userDAO));
    }

    public static DependencyRegistry getInstance() {
        return instance;
    }

    public <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    public <T> T get(Class<T> type) {
        return type.cast(services.get(type));
    }
}