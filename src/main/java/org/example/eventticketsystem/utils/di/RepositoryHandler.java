package org.example.eventticketsystem.utils.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles processing of classes annotated with @Repository.
 */
public class RepositoryHandler implements AnnotationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryHandler.class);

    @Override
    public void handle(Class<?> clazz, Injector injector) {
        LOGGER.info("Processing @Repository class: {}", clazz.getSimpleName());
        // Delegate to InjectableHandler for shared logic
        new InjectableHandler().handle(clazz, injector);
    }
}
