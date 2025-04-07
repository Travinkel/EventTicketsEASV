package org.example.eventticketsystem.bll.services.interfaces;

import org.example.eventticketsystem.dal.models.SpecialTicket;

import java.util.List;
import java.util.Optional;

public interface ISpecialTicketService {

    // ==== Opret / Administrér SpecialTickets ====

    boolean createSpecialTicket(SpecialTicket ticket);
    boolean deleteSpecialTicket(int id, int issuedBy);
    boolean assignSpecialTicketToUser(int specialTicketId, int userId);
    Optional<SpecialTicket> getSpecialTicketById(int id);

    // ==== Koordinatorrelateret funktionalitet ====

    List<SpecialTicket> getAllIssuedBy(int coordinatorId);
    List<SpecialTicket> getSpecialTicketsForEvent(int eventId);
    List<SpecialTicket> getUnassignedTicketsForEvent(int eventId);

    // ==== Brugerbaseret adgang (valgfrit hvis nødvendigt) ====

    List<SpecialTicket> getTicketsForUser(int userId);
}
