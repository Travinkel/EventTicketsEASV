package org.example.eventticketsystem.bll;

import org.example.eventticketsystem.dal.EventDAO;
import org.example.eventticketsystem.models.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventService {

    private final EventDAO eventDAO;

    public EventService(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    // === Basic CRUD ===

    public List<Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }

    public Optional<Event> getEventById(int eventId) {
        return eventDAO.getEventById(eventId);
    }

    public boolean createEvent(Event event) {
        return eventDAO.saveEvent(event);
    }

    public boolean updateEvent(Event event) {
        return eventDAO.updateEvent(event);
    }

    public boolean deleteEvent(int eventId) {
        return eventDAO.deleteEvent(eventId);
    }

    public List<Event> getEventsByCoordinator(int coordinatorId) {
        return eventDAO.getEventsByCoordinatorId(coordinatorId);
    }

    // === Future-Proofed Business Methods ===

    // Active events on a given day
    public List<Event> getEventsOnDate(LocalDate date) {
        return eventDAO.getAllEvents().stream()
                .filter(e -> !e.getStartTime().toLocalDate().isAfter(date)
                        && !e.getEndTime().toLocalDate().isBefore(date))
                .collect(Collectors.toList());
    }

    // Events grouped by date (for calendar views)
    public Map<LocalDate, List<Event>> groupEventsByDay() {
        return eventDAO.getAllEvents().stream()
                .collect(Collectors.groupingBy(e -> e.getStartTime().toLocalDate()));
    }

    // Upcoming events
    public List<Event> getUpcomingEvents() {
        return eventDAO.getAllEvents().stream()
                .filter(e -> e.getStartTime().isAfter(LocalDate.now().atStartOfDay()))
                .collect(Collectors.toList());
    }

    // Events by location
    public Map<String, List<Event>> groupEventsByLocation() {
        return eventDAO.getAllEvents().stream()
                .collect(Collectors.groupingBy(Event::getLocation));
    }

    // Get events between two dates
    public List<Event> getEventsInRange(LocalDate start, LocalDate end) {
        return eventDAO.getAllEvents().stream()
                .filter(e -> !e.getStartTime().toLocalDate().isAfter(end)
                        && !e.getEndTime().toLocalDate().isBefore(start))
                .collect(Collectors.toList());
    }

    public long countEventsByCoordinator(int userId) {
        return eventDAO.getAllEvents().stream()
                .filter(e -> e.getCoordinatorId() == userId)
                .count();
    }
}
