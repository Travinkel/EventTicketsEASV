package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.bll.services.interfaces.IEventCoordinatorService;
import org.example.eventticketsystem.bll.viewmodels.TicketComposite;
import org.example.eventticketsystem.dal.dao.*;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.SpecialTicket;
import org.example.eventticketsystem.dal.models.Ticket;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventCoordinatorService implements IEventCoordinatorService {

    private final EventRepository
            eventRepository;
    private final TicketRepository
            ticketRepository;
    private final SpecialTicketRepository
            specialTicketRepository;
    private final UserEventRoleRepository
            userEventRoleRepository;
    private final UserRepository
            userRepository;

    public EventCoordinatorService(EventRepository eventRepository,
                                   TicketRepository ticketRepository,
                                   SpecialTicketRepository specialTicketRepository,
                                   UserEventRoleRepository userEventRoleRepository,
                                   UserRepository userRepository) {
        this.eventRepository =
                eventRepository;
        this.ticketRepository =
                ticketRepository;
        this.specialTicketRepository =
                specialTicketRepository;
        this.userEventRoleRepository =
                userEventRoleRepository;
        this.userRepository =
                userRepository;
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
    public boolean createEvent(Event event,
                               int coordinatorId) {
        boolean
                created =
                eventRepository.save(event);
        if (created) {
            // Assign coordinator role immediately
            return userEventRoleRepository.assignCoordinatorToEvent(coordinatorId,
                    event.getId());
        }
        return false;
    }

    @Override
    public boolean updateEvent(Event event) {
        return eventRepository.update(event);
    }

    @Override
    public boolean deleteEvent(int eventId,
                               int coordinatorId) {
        // Optional: Check if coordinator is allowed to delete it
        List<Event>
                myEvents =
                userEventRoleRepository.getEventsByUserId(coordinatorId);
        boolean
                ownsEvent =
                myEvents.stream()
                        .anyMatch(e -> e.getId() ==
                                       eventId);
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
    public boolean issueSpecialTicket(SpecialTicket ticket) {
        return specialTicketRepository.save(ticket);
    }

    // === SPECIAL TICKETS ===

    @Override
    public List<SpecialTicket> getSpecialTicketsForEvent(int eventId) {
        return specialTicketRepository.findByEventId(eventId);
    }

    @Override
    public boolean revokeSpecialTicket(int specialTicketId) {
        return specialTicketRepository.delete(specialTicketId);
    }


    @Override
    public boolean assignCoordinatorToEvent(int eventId,
                                            int coordinatorId) {
        return userEventRoleRepository.assignCoordinatorToEvent(coordinatorId,
                eventId);
    }

    // === COORDINATOR ASSIGNMENT ===

    /**
     * Removes the coordinator role from a user for a specific event.
     *
     * @param eventId       The ID
     *                      of the event.
     * @param coordinatorId The ID of the user to remove the role from.
     * @return True if the operation was successful, false otherwise.
     */
    @Override
    public boolean removeCoordinatorFromEvent(int eventId,
                                              int coordinatorId) {
        return userEventRoleRepository.removeCoordinatorFromEvent(coordinatorId,
                eventId);
    }


    @Override
    public List<User> getCoordinatorsForEvent(int eventId) {
        return userEventRoleRepository.getCoordinatorsForEvent(eventId);
    }

    /**
     * Finds a ticket by its ID.
     *
     * @param ticketId The ID of the ticket to find.
     * @return The ticket if found, or null if not.
     */
    @Override
    public Ticket getTicketById(int ticketId) {
        return ticketRepository.findById(ticketId)
                .orElse(null);
    }


    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElse(null);
    }

    /**
     * Retrieves the email of the user with the given ID, or null if no such user exists.
     *
     * @param userId The ID of the user.
     * @return The user's email or null.
     */
    @Override
    public String getUserEmailById(int userId) {
        User
                user =
                userRepository.findById(userId)
                        .orElse(null);
        return user !=
               null ?
                user.getEmail() :
                null;
    }

    /**
     * Finds all events where the user is an "EVENTCOORDINATOR".
     *
     * @param coordinatorId The ID of the user.
     * @return A list of {@link Event} objects, or an empty list if none are found.
     */
    @Override
    public List<Event> getEventsForCoordinator(int coordinatorId) {
        return userEventRoleRepository.getEventsByUserId(coordinatorId);
    }

    /**
     * Retrieves a list of ticket composites (ticket information + user email + price) for a given event.
     * <p>
     * The price is retrieved from the ticket itself (if it is stored there). If the price is stored elsewhere
     * (e.g. in TicketType or EventType), the retrieval of the price should be adjusted accordingly.
     *
     * @param eventId The ID of the event.
     * @return A list of {@link TicketComposite} objects.
     */
    @Override
    public List<TicketComposite> findTicketsForEvent(int eventId) {
        List<Ticket>
                tickets =
                ticketRepository.findTicketsByEventId(eventId);
        List<TicketComposite>
                result =
                new ArrayList<>();

        for (Ticket t : tickets) {
            User
                    user =
                    userRepository.findById(t.getUserId())
                            .orElse(null);
            if (user ==
                null) {
                continue;
            }

            // If price is stored in Ticket directly:
            double
                    price =
                    t.getPriceAtPurchase();

            // If it's stored elsewhere (e.g. TicketType or EventType), adjust here.
            result.add(new TicketComposite(t,
                    user.getEmail(),
                    price));
        }

        return result;
    }
}
