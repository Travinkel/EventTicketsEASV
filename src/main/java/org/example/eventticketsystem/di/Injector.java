package org.example.eventticketsystem.di;

import org.example.eventticketsystem.exceptions.DependencyInjectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Injector {
    private static final Logger LOGGER = LoggerFactory.getLogger(Injector.class);
    private static Injector instance;
    private final Map<Class<?>, Object> dependencies = new ConcurrentHashMap<>();

    private Injector() {}

    public static synchronized Injector getInstance() {
        if (instance == null) {
            instance = new Injector();
        }
        return instance;
    }

    public <T> void register(Class<T> clazz, T instance) {
        LOGGER.debug("Registering dependency: {}", clazz.getSimpleName());
        dependencies.put(clazz, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) dependencies.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T createInstance(Class<T> clazz) {
        try {
            LOGGER.debug("Creating instance for: {}", clazz.getSimpleName());

            // ✅ FIXED: Pick constructor with most parameters
            var constructor = Arrays.stream(clazz.getConstructors())
                    .max(Comparator.comparingInt(Constructor::getParameterCount))
                    .orElseThrow(() -> new DependencyInjectionException("No suitable constructor found for " + clazz.getName(), null));

            Object[] params = Arrays.stream(constructor.getParameterTypes())
                    .map(param -> {
                        Object dep = get(param);
                        if (dep == null) {
                            dep = createInstance(param); // lazy-create missing dependencies
                        }
                        return dep;
                    })
                    .toArray();

            T createdInstance = (T) constructor.newInstance(params);
            register(clazz, createdInstance);
            return createdInstance;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DependencyInjectionException("Failed to instantiate " + clazz.getName(), e);
        }
    }
}
