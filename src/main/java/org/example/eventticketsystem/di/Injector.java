package org.example.eventticketsystem.di;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.example.eventticketsystem.bll.EmailService;
import org.example.eventticketsystem.bll.EventService;
import org.example.eventticketsystem.bll.TicketPurchaseService;
import org.example.eventticketsystem.bll.TicketService;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.dal.EventDAO;
import org.example.eventticketsystem.dal.TicketDAO;
import org.example.eventticketsystem.dal.UserDAO;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Injector {

    private static Injector instance;
    private final Map<Class<?>, Object> dependencies = new ConcurrentHashMap<>();

    private Injector() {}

    public static synchronized Injector getInstance() {
        if (instance == null) {
            instance = new Injector();
        }
        return instance;
    }

    public <T> void register(Class<T> clazz, T instance ) {
        dependencies.put(clazz, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) dependencies.get(clazz);
    }

    public <T> T createInstance(Class<T> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructors()[0];
            Object[] params = Arrays.stream(constructor.getParameterTypes())
                    .map(this::get)
                    .toArray();
            T instance = (T) constructor.newInstance(params);
            register(clazz, instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
        }
    }
}
