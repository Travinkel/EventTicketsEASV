package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.bll.services.interfaces.IEventCoordinatorService;
import org.example.eventticketsystem.dal.dao.*;
import org.example.eventticketsystem.dal.models.*;
import org.example.eventticketsystem.utils.di.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventCoordinatorService implements IEventCoordinatorService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final SpecialTicketRepository specialTicketRepository;
    private final UserEventRoleRepository userEventRoleRepository;
    private final UserRepository userRepository;

    public EventCoordinatorService(EventRepository eventRepository,
                                   TicketRepository ticketRepository,
                                   SpecialTicketRepository specialTicketRepository,
                                   UserEventRoleRepository userEventRoleRepository,
                                   UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.specialTicketRepository = specialTicketRepository;
        this.userEventRoleRepository = userEventRoleRepository;
        this.userRepository = userRepository;
    }

    // === EVENTS ===

    @Override
    public List<Event> getMyEvents(int coordinatorId) {
        return userEventRoleRepository.getEventsByUserId(coordinatorId);
    }

    @Override
    public Optional<Event> findEventById(int eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    public boolean createEvent(Event event, int coordinatorId) {
        boolean created = eventRepository.save(event);
        if (created) {
            // Assign coordinator role immediately
            return userEventRoleRepository.assignCoordinatorToEvent(coordinatorId, event.getId());
        }
        return false;
    }

    @Override
    public boolean updateEvent(Event event) {
        return eventRepository.update(event);
    }

    @Override
    public boolean deleteEvent(int eventId, int coordinatorId) {
        // Optional: Check if coordinator is allowed to delete it
        List<Event> myEvents = userEventRoleRepository.getEventsByUserId(coordinatorId);
        boolean ownsEvent = myEvents.stream().anyMatch(e -> e.getId() == eventId);
        if (!ownsEvent) return false;

        return eventRepository.delete(eventId);
    }

    // === STANDARD TICKETS ===

    @Override
    public boolean createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> getTicketsForEvent(int eventId) {
        return ticketRepository.findTicketsByEventId(eventId);
    }

    @Override
    public boolean deleteTicket(int ticketId) {
        return ticketRepository.delete(ticketId);
    }

    @Override
    public Ticket getTicketById(int ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }

    // === SPECIAL TICKETS ===

    @Override
    public boolean issueSpecialTicket(SpecialTicket ticket) {
        return specialTicketRepository.save(ticket);
    }

    @Override
    public List<SpecialTicket> getSpecialTicketsForEvent(int eventId) {
        return specialTicketRepository.findByEventId(eventId);
    }

    @Override
    public boolean revokeSpecialTicket(int specialTicketId) {
        return specialTicketRepository.delete(specialTicketId);
    }

    // === COORDINATOR ASSIGNMENT ===

    @Override
    public boolean assignCoordinatorToEvent(int eventId, int coordinatorId) {
        return userEventRoleRepository.assignCoordinatorToEvent(coordinatorId, eventId);
    }

    @Override
    public boolean removeCoordinatorFromEvent(int eventId, int coordinatorId) {
        return userEventRoleRepository.removeCoordinatorFromEvent(coordinatorId, eventId);
    }

    @Override
    public List<User> getCoordinatorsForEvent(int eventId) {
        return userEventRoleRepository.getCoordinatorsForEvent(eventId);
    }

    @Override
    public List<Event> getEventsForCoordinator(int coordinatorId) {
        return userEventRoleRepository.getEventsByUserId(coordinatorId);
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
