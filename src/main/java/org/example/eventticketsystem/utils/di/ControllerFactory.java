package org.example.eventticketsystem.utils.di;

import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating controller instances using dependency injection.
 * Implements the JavaFX Callback interface to provide controllers to the framework.
 */
public class ControllerFactory implements Callback<Class<?>, Object> {
    private static final Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

    // Singleton instance of the Injector for managing dependencies.
    private final Injector injector = Injector.getInstance();

    /**
     * Creates an instance of the specified controller class.
     *
     * @param clazz the class of the controller to be created
     * @return the created controller instance
     * @throws RuntimeException if the controller creation fails
     */
    @Override
    public Object call(Class<?> clazz) {
        logger.debug("Creating controller: {}", clazz.getSimpleName());
        try {
            return injector.createInstance(clazz);
        } catch (Exception e) {
            logger.error("Failed to create controller: {}", clazz.getSimpleName(), e);
            throw new RuntimeException("Controller creation failed for " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Provides a string representation of the ControllerFactory.
     *
     * @return a string describing the factory
     */
    @Override
    public String toString() {
        return "ControllerFactory{" +
                "injector=" + injector +
                '}';
    }
}
