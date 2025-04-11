package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.bll.viewmodels.TicketComposite;
import org.example.eventticketsystem.dal.dao.*;
import org.example.eventticketsystem.dal.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventCoordinatorServiceTest {

    private EventCoordinatorService service;
    private EventRepository eventRepository;
    private TicketRepository ticketRepository;
    private SpecialTicketRepository specialTicketRepository;
    private UserEventRoleRepository userEventRoleRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        ticketRepository = mock(TicketRepository.class);
        specialTicketRepository = mock(SpecialTicketRepository.class);
        userEventRoleRepository = mock(UserEventRoleRepository.class);
        userRepository = mock(UserRepository.class);

        service = new EventCoordinatorService(
                eventRepository,
                ticketRepository,
                specialTicketRepository,
                userEventRoleRepository,
                userRepository
        );
    }

    @Test
    void testGetMyEvents() {
        int coordinatorId = 1;
        List<Event> mockEvents = Arrays.asList(new Event(1, "Event 1"), new Event(2, "Event 2"));
        when(userEventRoleRepository.getEventsByUserId(coordinatorId)).thenReturn(mockEvents);

        List<Event> result = service.getMyEvents(coordinatorId);

        assertEquals(2, result.size());
        assertEquals("Event 1", result.get(0).getName());
        verify(userEventRoleRepository, times(1)).getEventsByUserId(coordinatorId);
    }

    @Test
    void testFindEventById() {
        int eventId = 1;
        Event mockEvent = new Event(eventId, "Event 1");
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

        Optional<Event> result = service.findEventById(eventId);

        assertTrue(result.isPresent());
        assertEquals("Event 1", result.get().getName());
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void testCreateEvent() {
        Event event = new Event(1, "New Event");
        int coordinatorId = 1;
        when(eventRepository.save(event)).thenReturn(true);
        when(userEventRoleRepository.assignCoordinatorToEvent(coordinatorId, event.getId())).thenReturn(true);

        boolean result = service.createEvent(event, coordinatorId);

        assertTrue(result);
        verify(eventRepository, times(1)).save(event);
        verify(userEventRoleRepository, times(1)).assignCoordinatorToEvent(coordinatorId, event.getId());
    }

    @Test
    void testDeleteEvent() {
        int eventId = 1;
        int coordinatorId = 1;
        Event mockEvent = new Event(eventId, "Event 1");
        when(userEventRoleRepository.getEventsByUserId(coordinatorId)).thenReturn(List.of(mockEvent));
        when(eventRepository.delete(eventId)).thenReturn(true);

        boolean result = service.deleteEvent(eventId, coordinatorId);

        assertTrue(result);
        verify(eventRepository, times(1)).delete(eventId);
    }

    @Test
    void testFindTicketsForEvent() {
        int eventId = 1;
        Ticket ticket = new Ticket(1, eventId, 1, 100.0);
        User user = new User(1, "test@example.com");
        when(ticketRepository.findTicketsByEventId(eventId)).thenReturn(List.of(ticket));
        when(userRepository.findById(ticket.getUserId())).thenReturn(Optional.of(user));

        List<TicketComposite> result = service.findTicketsForEvent(eventId);

        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getUserEmail());
        assertEquals(100.0, result.get(0).getPrice());
        verify(ticketRepository, times(1)).findTicketsByEventId(eventId);
        verify(userRepository, times(1)).findById(ticket.getUserId());
    }

    // Add more tests for other methods as needed...
}
