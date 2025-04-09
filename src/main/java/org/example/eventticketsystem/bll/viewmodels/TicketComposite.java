package org.example.eventticketsystem.bll.viewmodels;

import org.example.eventticketsystem.dal.models.Ticket;

public class TicketComposite {
    private final Ticket ticket;
    private final String userEmail;
    private final double price;

    public TicketComposite(Ticket ticket, String userEmail, double price) {
        this.ticket = ticket;
        this.userEmail = userEmail;
        this.price = price;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public double getPrice() {
        return price;
    }

    public boolean isCheckedIn() {
        return ticket.isCheckedIn();
    }

    public int getTicketId() {
        return ticket.getId();
    }

    public int getUserId() {
        return ticket.getUserId();
    }
}
