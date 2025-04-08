package org.example.eventticketsystem.utils.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define the scope of a dependency (e.g., singleton or prototype).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    /**
     * The scope value (e.g., "singleton", "prototype").
     *
     * @return the scope value
     */
    String value();
}
