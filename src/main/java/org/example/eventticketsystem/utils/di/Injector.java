package org.example.eventticketsystem.utils.di;

import org.example.eventticketsystem.utils.di.exceptions.DependencyInjectionException;
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

    private Injector() {
    }

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

    public <T> T createInstance(Class<T> clazz) {
        try {
            LOGGER.debug("Creating instance for: {}", clazz.getSimpleName());

            // ✅ Block enums and internal types that can't be instantiated
            if (clazz.isEnum() || clazz.getName().startsWith("javafx.")) {
                throw new DependencyInjectionException("Cannot inject type: " + clazz.getName(), null);
            }

            Constructor<?>[] publicConstructors = clazz.getConstructors();
            if (publicConstructors.length == 0) {
                throw new DependencyInjectionException("No public constructor found for " + clazz.getName(), null);
            }

            var constructor = Arrays.stream(publicConstructors)
                    .max(Comparator.comparingInt(Constructor::getParameterCount))
                    .orElseThrow(() -> new DependencyInjectionException("No suitable constructor found for " + clazz.getName(), null));

            LOGGER.debug("Injecting dependencies for class: {}", clazz.getName());
            Object[] params = Arrays.stream(constructor.getParameterTypes())
                    .map(param -> {
                        LOGGER.debug(" → Resolving dependency: {}", param.getName());
                        Object dep = get(param);
                        if (dep == null) {
                            if (param.isEnum() || param.getName().startsWith("javafx.")) {
                                throw new DependencyInjectionException("Cannot inject type: " + param.getName(), null);
                            }
                            dep = createInstance(param); // lazy-create
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
