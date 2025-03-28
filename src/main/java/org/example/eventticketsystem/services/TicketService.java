/*
package org.example.eventticketsystem.services;

import org.example.eventticketsystem.dao.EventDAO;
import org.example.eventticketsystem.models.Event;

import java.util.List;

public class EventService {

    private final EventDAO eventDAO;

    public EventService(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    public List<Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }

    public boolean saveEvent(Event event) {
        return eventDAO.saveEvent(event);
    }

    public boolean deleteEvent(int eventId) {
        return eventDAO.deleteEvent(eventId);
    }

    public void sendTicketToCustomer(Ticket ticket, User customer) {
        if (customer.getRole() != UserRole.CUSTOMER) {
            System.err.println("❌ Not a customer: " + customer.getUsername());
            return;
        }
        try {
            byte[] pdfData = PDFGenerator.generalTicketPDF(ticket,customer);
            EmailService.sendEmailWithAttachment(
                    customer.getEmail(),
                    "Din billet til arrangement",
                    "Tak for din bestilling. Din billet er vedhæftet som PDF.",
                    pdfData, "EASV_Billet.pdf"
            );
            System.out.println("✅ Ticket sent to: " + customer.getEmail());
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send ticket email to: " + customer.getEmail());
        }
    }
}
*/
