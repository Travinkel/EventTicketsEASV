package org.example.eventticketsystem.utils;

import org.example.eventticketsystem.bll.*;
import org.example.eventticketsystem.dal.EventDAO;
import org.example.eventticketsystem.dal.TicketDAO;
import org.example.eventticketsystem.dal.UserDAO;

import java.util.HashMap;
import java.util.Map;

public class DependencyRegistry {

    private static DependencyRegistry instance;

    private final Map<Class<?>, Object> registry = new HashMap<>();

    private DependencyRegistry() {
        // DAOs
        UserDAO userDAO = new UserDAO();
        EventDAO eventDAO = new EventDAO();
        TicketDAO ticketDAO = new TicketDAO();

        // Services
        EmailService emailService = new EmailService();
        UserService userService = new UserService(userDAO);
        EventService eventService = new EventService(eventDAO);
        TicketService ticketService = new TicketService(ticketDAO);
        TicketPurchaseService ticketPurchaseService = new TicketPurchaseService(ticketDAO, emailService);

        // Register core services
        registry.put(UserService.class, userService);
        registry.put(EventService.class, eventService);
        registry.put(TicketService.class, ticketService);
        registry.put(TicketPurchaseService.class, ticketPurchaseService);
        registry.put(EmailService.class, emailService);
    }

    public static synchronized DependencyRegistry getInstance() {
        if (instance == null) {
            instance = new DependencyRegistry();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) registry.get(clazz);
    }

    public <T> void register(Class<T> clazz, T impl) {
        registry.put(clazz, impl);
    }
}