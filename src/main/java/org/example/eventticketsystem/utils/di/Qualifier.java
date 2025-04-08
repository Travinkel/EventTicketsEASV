package org.example.eventticketsystem.utils.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a named qualifier for dependency injection.
 * Used to differentiate between multiple implementations of the same type.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    /**
     * The name of the qualifier.
     *
     * @return the qualifier name
     */
    String value();

    // Default qualifier value
    String DEFAULT = "default";
}
