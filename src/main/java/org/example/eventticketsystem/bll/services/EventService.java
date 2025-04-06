package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.dal.dao.EventDAO;
import org.example.eventticketsystem.dal.dao.UserEventRoleDAO;
import org.example.eventticketsystem.dal.models.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventService {

    private final EventDAO eventDAO;
    private final UserEventRoleDAO userEventRoleDAO;

    public EventService(EventDAO eventDAO, UserEventRoleDAO userEventRoleDAO) {
        this.eventDAO = eventDAO;
        this.userEventRoleDAO = userEventRoleDAO;
    }

    // === Basic CRUD ===
    public List<Event> getAllEvents() {
        return eventDAO.findAll();
    }

    public Optional<Event> getEventById(int eventId) {
        return eventDAO.findById(eventId);
    }

    public boolean createEvent(Event event) {
        return eventDAO.save(event);
    }

    public boolean updateEvent(Event event) {
        return eventDAO.update(event);
    }

    public boolean deleteEvent(int eventId) {
        return eventDAO.delete(eventId);
    }

    // === Event-Role aware ===
    public List<Event> getEventsForCoordinator(int coordinatorId) {
        List<Integer> eventIds = userEventRoleDAO.getEventIdsForCoordinator(coordinatorId);
        return eventDAO.findAll().stream()
                .filter(e -> eventIds.contains(e.getId()))
                .collect(Collectors.toList());
    }

    public long countEventsForCoordinator(int coordinatorId) {
        return userEventRoleDAO.getEventIdsForCoordinator(coordinatorId).size();
    }

    public List<Event> getEventsByCoordinator(int userId) {
        List<Integer> eventIds = userEventRoleDAO.getEventIdsForCoordinator(userId);
        return eventDAO.findAll().stream()
                .filter(event -> eventIds.contains(event.getId()))
                .toList();
    }

    // === Business use cases ===
    public List<Event> getEventsOnDate(LocalDate date) {
        return eventDAO.findAll().stream()
                .filter(e -> !e.getStartTime().toLocalDate().isAfter(date)
                        && !e.getEndTime().toLocalDate().isBefore(date))
                .collect(Collectors.toList());
    }

    public Map<LocalDate, List<Event>> groupEventsByDay() {
        return eventDAO.findAll().stream()
                .collect(Collectors.groupingBy(e -> e.getStartTime().toLocalDate()));
    }

    public List<Event> getUpcomingEvents() {
        return eventDAO.findAll().stream()
                .filter(e -> e.getStartTime().isAfter(LocalDate.now().atStartOfDay()))
                .collect(Collectors.toList());
    }

    public Map<String, List<Event>> groupEventsByLocation() {
        return eventDAO.findAll().stream()
                .collect(Collectors.groupingBy(Event::getLocation));
    }

    public List<Event> getEventsInRange(LocalDate start, LocalDate end) {
        return eventDAO.findAll().stream()
                .filter(e -> !e.getStartTime().toLocalDate().isAfter(end)
                        && !e.getEndTime().toLocalDate().isBefore(start))
                .collect(Collectors.toList());
    }
}
