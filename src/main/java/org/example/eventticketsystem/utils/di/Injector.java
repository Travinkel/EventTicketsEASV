package org.example.eventticketsystem.utils.di;

import org.example.eventticketsystem.utils.di.exceptions.DependencyInjectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core class for managing dependency injection.
 * Handles the creation, registration, and retrieval of dependencies.
 */
public final class Injector {
    private static final Logger LOGGER = LoggerFactory.getLogger(Injector.class);
    private static Injector instance;
    private static final Map<Class<?>, Object> dependencies = new ConcurrentHashMap<>();

    // Private constructor to enforce singleton pattern.
    private Injector() {
    }

    /**
     * Retrieves the singleton instance of the Injector.
     *
     * @return the singleton instance
     */
    public static synchronized Injector getInstance() {
        if (instance == null) {
            instance = new Injector();
        }
        return instance;
    }

    public static synchronized <T> void register(Class<T> clazz, T instance) {
        LOGGER.debug("Registering dependency: {}", clazz.getSimpleName());

        dependencies.put(clazz, instance);

        for (Class<?> iface : clazz.getInterfaces()) {
            if (!dependencies.containsKey(iface)) {
                LOGGER.debug(" → Also registering interface {} for {}", iface.getSimpleName(), clazz.getSimpleName());
                dependencies.put(iface, instance);
            }
        }
    }

    /**
     * Registers a dependency with the injector.
     *
     * @param clazz    the class of the dependency
     * @param instance the instance of the dependency
     * @param scope    the scope of the dependency (e.g., singleton or prototype)
     * @param <T>      the type of the dependency
     */
    public static synchronized <T> void register(Class<T> clazz, T instance, String scope) {
        LOGGER.debug("Registering dependency: {} with scope: {}", clazz.getSimpleName(), scope);
        if (dependencies.containsKey(clazz)) {
            LOGGER.warn("⚠\uFE0F Dependency {} is already registered. Overwriting.", clazz.getSimpleName());
        } else {
            LOGGER.info("✅ Registering dependency: {}", clazz.getSimpleName());
        }
        dependencies.put(clazz, instance);
    }

    /**
     * Retrieves a registered dependency.
     *
     * @param clazz the class of the dependency
     * @param <T>   the type of the dependency
     * @return the registered dependency instance, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {
        return (T) dependencies.get(clazz);
    }

    /**
     * Creates an instance of the specified class and injects its dependencies.
     *
     * @param clazz the class to instantiate
     * @param <T>   the type of the class
     * @return the created instance
     * @throws DependencyInjectionException if instantiation fails
     */
    public <T> T createInstance(Class<T> clazz) {
        validateClassForInjection(clazz);

        if (clazz.isAnnotationPresent(Singleton.class)) {
            T singletonInstance = getSingletonInstance(clazz);
            if (singletonInstance != null) {
                return singletonInstance;
            }
        }

        try {
            LOGGER.debug("Creating instance for: {}", clazz.getSimpleName());
            Constructor<?> constructor = findInjectableConstructor(clazz);
            Object[] params = resolveConstructorParameters(constructor);

            @SuppressWarnings("unchecked")
            T instance = (T) constructor.newInstance(params);

            for (Class<?> iface : clazz.getInterfaces()) {
                if (!dependencies.containsKey(iface)) {
                    LOGGER.debug(" → Auto-binding interface {} to {}", iface.getSimpleName(), clazz.getSimpleName());
                    dependencies.put(iface, instance);
                }
            }

            if (clazz.isAnnotationPresent(Singleton.class)) {
                synchronized (this) {
                    register(clazz, instance, "singleton");
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Failed to instantiate {}: {}", clazz.getName(), e.getMessage(), e);
            throw new DependencyInjectionException("Failed to instantiate " + clazz.getName(), e);
        }
    }

    /**
     * Validates if a class is eligible for dependency injection.
     *
     * @param clazz the class to validate
     * @param <T>   the type of the class
     * @throws DependencyInjectionException if the class is not eligible
     */
    private <T> void validateClassForInjection(Class<T> clazz) {
        if (clazz.isEnum() || clazz.getName().startsWith("javafx.") || Modifier.isAbstract(clazz.getModifiers())) {
            throw new DependencyInjectionException("Cannot inject type: " + clazz.getName(), null);
        }

        if (!clazz.isAnnotationPresent(Injectable.class) &&
            !clazz.isAnnotationPresent(Service.class) &&
            !clazz.isAnnotationPresent(Repository.class) &&
            !clazz.isAnnotationPresent(Component.class)) { // Add Component check
            throw new DependencyInjectionException("Class " + clazz.getName() + " is not annotated with @Injectable, @Service, @Repository, or @Component", null);
        }
    }

    /**
     * Retrieves a singleton instance of the specified class, if available.
     *
     * @param clazz the class of the singleton
     * @param <T>   the type of the class
     * @return the singleton instance, or null if not found
     */
    public <T> T getSingletonInstance(Class<T> clazz) {
        T singletonInstance = get(clazz);
        if (singletonInstance != null) {
            LOGGER.debug("Returning cached singleton for: {}", clazz.getSimpleName());
        }
        return singletonInstance;
    }

    /**
     * Finds the most suitable constructor for dependency injection.
     *
     * @param clazz the class to inspect
     * @return the selected constructor
     * @throws DependencyInjectionException if no suitable constructor is found
     */
    private Constructor<?> findInjectableConstructor(Class<?> clazz) {
        Constructor<?>[] publicConstructors = clazz.getConstructors();
        if (publicConstructors.length == 0) {
            throw new DependencyInjectionException("No public constructor found for " + clazz.getName(), null);
        }

        return Arrays.stream(publicConstructors)
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElse(Arrays.stream(publicConstructors)
                        .max(Comparator.comparingInt(Constructor::getParameterCount))
                        .orElseThrow(() -> new DependencyInjectionException("No suitable constructor found for " + clazz.getName(), null)));
    }

    /**
     * Resolves the parameters for a constructor by injecting dependencies.
     *
     * @param constructor the constructor to resolve parameters for
     * @return an array of resolved parameters
     */
    private Object[] resolveConstructorParameters(Constructor<?> constructor) {
        return Arrays.stream(constructor.getParameterTypes())
                .map(param -> {
                    LOGGER.debug(" → Resolving dependency: {}", param.getName());
                    Object dep = get(param);
                    if (dep == null) {
                        dep = createInstance(param);
                    }
                    return dep;
                })
                .toArray();
    }
}
