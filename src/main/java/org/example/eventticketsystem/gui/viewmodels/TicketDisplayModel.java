package org.example.eventticketsystem.gui.viewmodels;

import org.example.eventticketsystem.dal.models.Ticket;
import org.example.eventticketsystem.dal.models.User;

public class TicketDisplayModel {
    private final int ticketId;
    private final int userId;
    private final String email;
    private final double price;
    private final boolean checkedIn;

    public TicketDisplayModel(Ticket ticket, User user) {
        this.ticketId = ticket.getId();
        this.userId = user.getId();
        this.email = user.getEmail();
        this.price = ticket.getPriceAtPurchase();
        this.checkedIn = ticket.isCheckedIn();
    }

    public int getTicketId() { return ticketId; }
    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public double getPrice() { return price; }
    public boolean isCheckedIn() { return checkedIn; }
}
