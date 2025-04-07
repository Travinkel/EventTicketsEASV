package org.example.eventticketsystem.bll.services.interfaces;

import org.example.eventticketsystem.dal.models.Ticket;

import java.util.List;

public interface ITicketService {
    void issueTicket(int userId, int eventId, double price);
    List<Ticket> getTicketsForEvent(int eventId);
    void checkInTicket(String qrCode);

}
