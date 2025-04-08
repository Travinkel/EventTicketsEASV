package org.example.eventticketsystem.utils.di;

/**
 * Interface for handling annotations during dependency injection scanning.
 */
public interface AnnotationHandler {
    /**
     * Processes a class annotated with a specific annotation.
     *
     * @param clazz    the class to process
     * @param injector the injector instance
     */
    void handle(Class<?> clazz, Injector injector);
}
