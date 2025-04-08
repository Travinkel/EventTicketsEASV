package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.bll.services.interfaces.ISpecialTicketService;
import org.example.eventticketsystem.dal.dao.SpecialTicketRepository;
import org.example.eventticketsystem.dal.models.SpecialTicket;
import org.example.eventticketsystem.utils.di.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialTicketService implements ISpecialTicketService {
    private final SpecialTicketRepository repository;

    public SpecialTicketService(SpecialTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean createSpecialTicket(SpecialTicket ticket) {
        return repository.save(ticket);
    }

    @Override
    public boolean deleteSpecialTicket(int ticketId, int issuedBy) {
        Optional<SpecialTicket> ticket = repository.findById(ticketId);
        return ticket.filter(t -> t.getIssuedBy() == issuedBy)
                .map(t -> repository.delete(ticketId))
                .orElse(false);
    }

    @Override
    public boolean assignSpecialTicketToUser(int ticketId, int userId) {
        return repository.assignToUser(ticketId, userId);
    }

    @Override
    public Optional<SpecialTicket> getSpecialTicketById(int ticketId) {
        return repository.findById(ticketId);
    }

    @Override
    public List<SpecialTicket> getAllIssuedBy(int coordinatorId) {
        return repository.findAllByIssuedBy(coordinatorId);
    }

    @Override
    public List<SpecialTicket> getSpecialTicketsForEvent(int eventId) {
        return repository.findByEventId(eventId);
    }

    @Override
    public List<SpecialTicket> getUnassignedTicketsForEvent(int eventId) {
        return repository.findUnassignedByEvent(eventId);
    }

    @Override
    public List<SpecialTicket> getTicketsForUser(int userId) {
        return repository.findByUserId(userId);
    }
}
