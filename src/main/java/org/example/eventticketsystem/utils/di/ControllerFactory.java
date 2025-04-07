package org.example.eventticketsystem.utils.di;

import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerFactory implements Callback<Class<?>, Object> {
    private static final Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

    private final Injector injector = Injector.getInstance();

    @Override
    public Object call(Class<?> clazz) {
        logger.debug("Creating controller: {}", clazz.getSimpleName());

        return injector.createInstance(clazz);
    }


    @Override
    public String toString() {
        return "ControllerFactory{" +
                "injector=" + injector +
                '}';
    }
}
