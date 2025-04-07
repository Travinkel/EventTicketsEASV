package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.dal.models.SpecialTicket;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class SpecialTicketRepository implements GenericRepository<SpecialTicket> {

    private static final Logger logger = LoggerFactory.getLogger(SpecialTicketRepository.class);
    private final DBConnection dbConnection;

    public SpecialTicketRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
        try {
            if (this.dbConnection == null || this.dbConnection.getConnection().isClosed()) {
                throw new IllegalStateException("❌ Cannot initialize SpecialTicketRepository: no DB connection");
            }
        } catch (SQLException e) {
            logger.error("Error validating DB connection", e);
        }
    }

    // ==== CRUD ====

    @Override
    public List<SpecialTicket> findAll() {
        String sql = "SELECT * FROM SpecialTickets";
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all special tickets", e);
        }
        return tickets;
    }

    @Override
    public Optional<SpecialTicket> findById(int id) {
        String sql = "SELECT * FROM SpecialTickets WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(ResultSetExtractor.extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding special ticket by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(SpecialTicket ticket) {
        String sql = "INSERT INTO SpecialTickets (event_id, user_id, type, issued_by, created_at, qrCode, barcode) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, ticket.getEventId(), Types.INTEGER);
            ps.setObject(2, ticket.getUserId(), Types.INTEGER);
            ps.setString(3, ticket.getType());
            ps.setInt(4, ticket.getIssuedBy());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getCreatedAt()));
            ps.setString(6, ticket.getQrCode());
            ps.setString(7, ticket.getBarcode());

            int rows = ps.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ticket.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error saving special ticket", e);
        }
        return false;
    }

    @Override
    public boolean update(SpecialTicket ticket) {
        String sql = "UPDATE SpecialTickets SET event_id = ?, user_id = ?, type = ?, issued_by = ?, created_at = ?, qrCode = ?, barcode = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, ticket.getEventId(), Types.INTEGER);
            ps.setObject(2, ticket.getUserId(), Types.INTEGER);
            ps.setString(3, ticket.getType());
            ps.setInt(4, ticket.getIssuedBy());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getCreatedAt()));
            ps.setString(6, ticket.getQrCode());
            ps.setString(7, ticket.getBarcode());
            ps.setInt(8, ticket.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("Error updating special ticket", e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM SpecialTickets WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("Error deleting special ticket", e);
        }
        return false;
    }

    public boolean assignToUser(int ticketId, int userId) {
        String sql = "UPDATE SpecialTickets SET user_id = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, ticketId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("Error assigning special ticket to user", e);
        }
        return false;
    }

    public List<SpecialTicket> findByUserId(int userId) {
        String sql = "SELECT * FROM SpecialTickets WHERE user_id = ?";
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding special tickets by userId", e);
        }
        return tickets;
    }

    public List<SpecialTicket> findByEventId(int eventId) {
        String sql = "SELECT * FROM SpecialTickets WHERE event_id = ?";
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding special tickets by eventId", e);
        }
        return tickets;
    }

    public List<SpecialTicket> findAllByIssuedBy(int issuedBy) {
        String sql = "SELECT * FROM SpecialTickets WHERE issued_by = ?";
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, issuedBy);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding special tickets by issuer", e);
        }
        return tickets;
    }

    public List<SpecialTicket> findUnassignedByEvent(int eventId) {
        String sql = "SELECT * FROM SpecialTickets WHERE event_id = ? AND user_id IS NULL";
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding unassigned special tickets by event", e);
        }
        return tickets;
    }
}
