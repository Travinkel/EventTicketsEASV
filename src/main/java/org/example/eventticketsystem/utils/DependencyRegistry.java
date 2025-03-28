package org.example.eventticketsystem.utils;

import java.util.HashMap;
import java.util.Map;

public class DependencyRegistry {
    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    public <T> T get(Class<T> type) {
        return type.cast(services.get(type));
    }
}