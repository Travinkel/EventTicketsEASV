package org.example.eventticketsystem.bll;

import org.example.eventticketsystem.dal.TicketDAO;
import org.example.eventticketsystem.models.Event;
import org.example.eventticketsystem.models.Ticket;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TicketService {

    private final TicketDAO ticketDAO;
    private final EventService eventService;


    public TicketService(TicketDAO ticketDAO, EventService eventService) {
        this.ticketDAO = ticketDAO;
        this.eventService = eventService;
    }

    // CRUD Operations
    public boolean saveTicket(Ticket ticket) {
        return ticketDAO.save(ticket);
    }

    public boolean updateTicket(Ticket ticket) {
        return ticketDAO.update(ticket);
    }

    public boolean deleteTicket(int id) {
        return ticketDAO.delete(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketDAO.findAll();
    }

    public Optional<Ticket> getTicketById(int id) {
        return ticketDAO.findById(id);
    }

    public int getTicketCount() {
        return ticketDAO.findAll().size();
    }

    /**
     * Bar chart - Monthly ticket sales (e.g. Jan → 120 tickets)
     */
    public Map<String, Integer> getMonthlyTicketSales() {
        return ticketDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        ticket -> ticket.getIssuedAt()
                                .getMonth()
                                .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("da")),
                        Collectors.reducing(0, e -> 1, Integer::sum)
                ));
    }

    // Count tickets per event
    public Map<Integer, Long> getTicketCountPerEvent() {
        return ticketDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        Ticket::getEventId,
                        Collectors.counting()
                ));
    }

    // Count tickets per user (optional: used for fraud detection or user analytics)
    public Map<Integer, Long> getTicketCountPerUser() {
        return ticketDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        Ticket::getUserId,
                        Collectors.counting()
                ));
    }

    // Tickets issued on a specific day
    public List<Ticket> getTicketsIssuedOn(LocalDate date) {
        return ticketDAO.findAll().stream()
                .filter(ticket -> ticket.getIssuedAt().toLocalDate().isEqual(date))
                .collect(Collectors.toList());
    }

    // Count issued tickets per day (for line chart over time)
    public Map<LocalDate, Long> getDailyTicketSales() {
        return ticketDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        ticket -> ticket.getIssuedAt().toLocalDate(),
                        Collectors.counting()
                ));
    }

    // Get all tickets for a specific event
    public List<Ticket> getTicketsForEvent(int eventId) {
        return ticketDAO.findAll().stream()
                .filter(ticket -> ticket.getEventId() == eventId)
                .collect(Collectors.toList());
    }

    // Get all tickets for a specific user
    public List<Ticket> getTicketsForUser(int userId) {
        return ticketDAO.findAll().stream()
                .filter(ticket -> ticket.getUserId() == userId)
                .collect(Collectors.toList());
    }

    /**
     * Check if a ticket already exists for a user for a given event
     */
    public boolean hasUserTicketForEvent(int userId, int eventId) {
        return ticketDAO.findAll().stream()
                .anyMatch(ticket -> ticket.getUserId() == userId && ticket.getEventId() == eventId);
    }
    public long countTicketsByCoordinator(int coordinatorId) {
        List<Event> coordinatorEvents = eventService.getEventsByCoordinator(coordinatorId);
        Set<Integer> eventIds = coordinatorEvents.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        return ticketDAO.findAll().stream()
                .filter(t -> eventIds.contains(t.getEventId()))
                .count();
    }

    public Map<String, Long> getTicketCountPerEvent(int coordinatorId) {
        List<Event> coordinatorEvents = eventService.getEventsByCoordinator(coordinatorId);

        return coordinatorEvents.stream()
                .collect(Collectors.toMap(
                        Event::getTitle,
                        event -> ticketDAO.findAll().stream()
                                .filter(t -> t.getEventId() == event.getId())
                                .count()
                ));
    }

    public Map<String, Double> getRevenuePerEvent(int coordinatorId) {
        List<Event> coordinatorEvents = eventService.getEventsByCoordinator(coordinatorId);

        return coordinatorEvents.stream()
                .collect(Collectors.toMap(
                        Event::getTitle,
                        event -> ticketDAO.findAll().stream()
                                .filter(t -> t.getEventId() == event.getId())
                                .mapToDouble(t -> event.getPrice()) // Assume fixed price per event
                                .sum()
                ));
    }

}
