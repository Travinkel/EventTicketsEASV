package org.example.eventticketsystem.bll;

import org.example.eventticketsystem.dal.TicketDAO;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.models.Event;
import org.example.eventticketsystem.models.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

@Injectable
public class TicketPurchaseService {
    private static final Logger logger = LoggerFactory.getLogger(TicketPurchaseService.class);

    private final TicketDAO ticketDAO;
    private final EmailService emailService;
    private final EventService eventService;

    public TicketPurchaseService(TicketDAO ticketDAO, EmailService emailService, EventService eventService) {
        this.ticketDAO = ticketDAO;
        this.emailService = emailService;
        this.eventService = eventService;
    }

    public boolean purchaseTicket(int eventId, int userId) {
        Event event = eventService.getEventById(eventId).orElseThrow();

        String qrCode = UUID.randomUUID().toString();

        Ticket ticket = new Ticket(
                0,
                eventId,
                userId,
                qrCode,
                LocalDateTime.now(),
                false,
                event.getPrice()
        );

        boolean success = ticketDAO.save(ticket);
        if (success) {
            logger.info("✅ Ticket saved for user {} to event {}", userId, eventId);
            emailService.sendTicketToUser(userId, ticket);
        } else {
            logger.warn("❌ Ticket could not be saved for user {} to event {}", userId, eventId);
        }
        return success;
    }
}
