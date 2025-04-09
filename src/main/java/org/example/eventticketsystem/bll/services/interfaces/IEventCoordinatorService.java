package org.example.eventticketsystem.bll.services.interfaces;

import org.example.eventticketsystem.bll.viewmodels.TicketComposite;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.SpecialTicket;
import org.example.eventticketsystem.dal.models.Ticket;
import org.example.eventticketsystem.dal.models.User;

import java.util.List;
import java.util.Optional;

public interface IEventCoordinatorService {

    // Events
    List<Event> getMyEvents(int coordinatorId); // Only coordinator’s events

    Optional<Event> findEventById(int eventId);

    boolean createEvent(Event event, int coordinatorId); // Assign on creation

    boolean updateEvent(Event event);

    boolean deleteEvent(int eventId, int coordinatorId); // Must validate permission

    // Tickets (Standard)
    boolean createTicket(Ticket ticket);

    List<Ticket> getTicketsForEvent(int eventId);

    boolean deleteTicket(int ticketId);

    // Special Tickets (Free / Discounted)
    boolean issueSpecialTicket(SpecialTicket ticket); // set issued_by = coordinatorId

    List<SpecialTicket> getSpecialTicketsForEvent(int eventId);

    boolean revokeSpecialTicket(int specialTicketId);

    // Optional: Assign other coordinators to the same event
    boolean assignCoordinatorToEvent(int eventId, int coordinatorId);

    boolean removeCoordinatorFromEvent(int eventId, int coordinatorId);

    List<User> getCoordinatorsForEvent(int eventId);

    // Additional methods
    Ticket getTicketById(int ticketId);

    User getUserById(int userId);

    String getUserEmailById(int userId); // New method

    List<Event> getEventsForCoordinator(int coordinatorId);

    List<TicketComposite> findTicketsForEvent(int eventId);
}
