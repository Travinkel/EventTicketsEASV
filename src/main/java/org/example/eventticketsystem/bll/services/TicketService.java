package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.dal.dao.SpecialTicketDAO;
import org.example.eventticketsystem.dal.dao.TicketDAO;
import org.example.eventticketsystem.dal.dao.UserEventRoleDAO;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.SpecialTicket;
import org.example.eventticketsystem.dal.models.Ticket;
import org.example.eventticketsystem.utils.PDFGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class TicketService {
    private final TicketDAO ticketDAO;
    private final SpecialTicketDAO specialTicketDAO;
    private final EmailService emailService;
    private final EventService eventService;
    private final UserEventRoleDAO userEventRoleDAO;

    public TicketService(
            TicketDAO ticketDAO,
            SpecialTicketDAO specialTicketDAO,
            EmailService emailService,
            EventService eventService,
            UserEventRoleDAO userEventRoleDAO
    ) {
        this.ticketDAO = ticketDAO;
        this.specialTicketDAO = specialTicketDAO;
        this.emailService = emailService;
        this.eventService = eventService;
        this.userEventRoleDAO = userEventRoleDAO;
    }

    // === CRUD ===

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

    // === Dashboard Analytics ===

    public Map<String, Integer> getMonthlyTicketSales() {
        return ticketDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        ticket -> ticket.getIssuedAt()
                                .getMonth()
                                .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("da")),
                        Collectors.reducing(0, e -> 1, Integer::sum)
                ));
    }

    public Map<Integer, Long> getTicketCountPerEvent() {
        return ticketDAO.findAll().stream()
                .collect(Collectors.groupingBy(Ticket::getEventId, Collectors.counting()));
    }

    public Map<Integer, Long> getTicketCountPerUser() {
        return ticketDAO.findAll().stream()
                .collect(Collectors.groupingBy(Ticket::getUserId, Collectors.counting()));
    }

    public List<Ticket> getTicketsIssuedOn(LocalDate date) {
        return ticketDAO.findAll().stream()
                .filter(ticket -> ticket.getIssuedAt().toLocalDate().isEqual(date))
                .collect(Collectors.toList());
    }

    public Map<LocalDate, Long> getDailyTicketSales() {
        return ticketDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        ticket -> ticket.getIssuedAt().toLocalDate(),
                        Collectors.counting()
                ));
    }

    public List<Ticket> getTicketsForEvent(int eventId) {
        return ticketDAO.findAll().stream()
                .filter(ticket -> ticket.getEventId() == eventId)
                .collect(Collectors.toList());
    }

    public List<Ticket> getTicketsForUser(int userId) {
        return ticketDAO.findAll().stream()
                .filter(ticket -> ticket.getUserId() == userId)
                .collect(Collectors.toList());
    }

    // === Issuing Tickets ===

    public boolean hasUserTicketForEvent(int userId, int eventId) {
        return ticketDAO.findAll().stream()
                .anyMatch(ticket -> ticket.getUserId() == userId && ticket.getEventId() == eventId);
    }

    public long countTicketsByCoordinator(int coordinatorId) {
        Set<Integer> eventIds = new HashSet<>(userEventRoleDAO.getEventIdsForCoordinator(coordinatorId));
        return ticketDAO.findAll().stream()
                .filter(t -> eventIds.contains(t.getEventId()))
                .count();
    }

    public Map<String, Long> getTicketCountPerEvent(int coordinatorId) {
        Set<Integer> eventIds = new HashSet<>(userEventRoleDAO.getEventIdsForCoordinator(coordinatorId));
        return eventService.getAllEvents().stream()
                .filter(e -> eventIds.contains(e.getId()))
                .collect(Collectors.toMap(
                        Event::getTitle,
                        event -> ticketDAO.findAll().stream()
                                .filter(t -> t.getEventId() == event.getId())
                                .count()
                ));
    }

    public Map<String, Double> getRevenuePerEvent(int coordinatorId) {
        Set<Integer> eventIds = new HashSet<>(userEventRoleDAO.getEventIdsForCoordinator(coordinatorId));
        return eventService.getAllEvents().stream()
                .filter(e -> eventIds.contains(e.getId()))
                .collect(Collectors.toMap(
                        Event::getTitle,
                        event -> ticketDAO.findAll().stream()
                                .filter(t -> t.getEventId() == event.getId())
                                .mapToDouble(Ticket::getPriceAtPurchase)
                                .sum()
                ));
    }

    // === Delivery / Print ===

    public boolean issueTicket(Event event, int userId, String userEmail) {
        Ticket ticket = createTicket(event, userId);
        if (!ticketDAO.save(ticket)) return false;
        return emailTicket(ticket, event, userEmail);
    }

    public Optional<String> printTicket(Event event, int userId) {
        Ticket ticket = createTicket(event, userId);
        if (!ticketDAO.save(ticket)) return Optional.empty();
        return Optional.of(PDFGenerator.generate(ticket, event));
    }

    // === Special Tickets ===

    public boolean issueSpecialTicket(SpecialTicket special) {
        return specialTicketDAO.save(special);
    }

    public List<SpecialTicket> getSpecialTicketsForEvent(int eventId) {
        return specialTicketDAO.findByEventId(eventId);
    }

    // === Internal Helpers ===

    private Ticket createTicket(Event event, int userId) {
        String code = UUID.randomUUID().toString();
        return new Ticket(
                0,
                event.getId(),
                userId,
                code,
                code.substring(0, 12),
                LocalDateTime.now(),
                false,
                event.getPrice()
        );
    }

    private boolean emailTicket(Ticket ticket, Event event, String userEmail) {
        String pdf = PDFGenerator.generate(ticket, event);
        return emailService.sendEmailWithAttachment(
                userEmail,
                "Din billet til " + event.getTitle(),
                "Vedhæftet finder du din billet med QR og stregkode.",
                pdf
        );
    }
}
