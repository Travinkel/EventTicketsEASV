package org.example.eventticketsystem.utils.di;

import org.example.eventticketsystem.utils.di.exceptions.DependencyInjectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles processing of classes annotated with @Component.
 */
public class ComponentHandler implements AnnotationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentHandler.class);

    @Override
    public void handle(Class<?> clazz, Injector injector) {
        try {
            Scope scope = clazz.getAnnotation(Scope.class);
            if (scope != null) {
                LOGGER.info("🔄 Registering scoped @Component class: {} with scope: {}", clazz.getSimpleName(), scope.value());
                String scopeValue = scope.value();
                if ("singleton".equals(scopeValue)) {
                    Object existingInstance = injector.getSingletonInstance(clazz);
                    if (existingInstance != null) {
                        LOGGER.info("🔄 Using existing singleton instance for class: {}", clazz.getSimpleName());
                        injector.register((Class<Object>) clazz, existingInstance, scopeValue);
                        return;
                    }
                }
            }

            Object instance = injector.createInstance(clazz);
            injector.register((Class<Object>) clazz, instance, null);
            LOGGER.info("✅ Injecting @Component class: {}", clazz.getSimpleName());
        } catch (DependencyInjectionException e) {
            LOGGER.error("❌ Failed to inject @Component {}: {}", clazz.getSimpleName(), e.getMessage());
        }
    }
}
