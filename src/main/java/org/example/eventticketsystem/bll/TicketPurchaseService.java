package org.example.eventticketsystem.bll;

import org.example.eventticketsystem.dal.TicketDAO;
import org.example.eventticketsystem.models.Ticket;
import org.example.eventticketsystem.utils.PDFGenerator;

import java.time.LocalDateTime;

public class TicketPurchaseService {

    private final TicketDAO ticketDAO;
    private final EmailService emailService;

    public TicketPurchaseService(TicketDAO ticketDAO, EmailService emailService) {
        this.ticketDAO = ticketDAO;
        this.emailService = emailService;
    }

    public boolean purchaseTicket(int eventId, int userId, String qrCode, String customerEmail, String outputDir) {
        Ticket ticket = new Ticket(0, eventId, userId, qrCode, LocalDateTime.now());

        boolean saved = ticketDAO.save(ticket);
        if (!saved) return false;

        String pdfPath = PDFGenerator.generateTicketPDF(ticket, outputDir);
        if (pdfPath == null) return false;

        return emailService.sendEmailWithAttachment(
                customerEmail,
                "Din billet til EASV Arrangement",
                "Hej! Her er din billet. Medbring den til arrangementet.",
                pdfPath
        );
    }
}
