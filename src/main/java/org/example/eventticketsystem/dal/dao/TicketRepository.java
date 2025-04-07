package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.dal.models.Ticket;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class TicketRepository implements GenericRepository<Ticket> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketRepository.class);
    private final DBConnection dbConnection;

    public TicketRepository(DBConnection dbConnection) throws SQLException {
        this.dbConnection = dbConnection;
        if (this.dbConnection == null || this.dbConnection.getConnection().isClosed()) {
            throw new IllegalStateException("❌ Cannot initialize TicketRepository: no DB connection");
        }
    }

    // ==== CRUD ====

    @Override
    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets";
        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(ResultSetExtractor.extractTicket(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding all tickets", e);
        }
        return tickets;
    }

    @Override
    public Optional<Ticket> findById(int id) {
        String sql = "SELECT * FROM Tickets WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(ResultSetExtractor.extractTicket(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding ticket by id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Ticket ticket) {
        String sql = "INSERT INTO Tickets (eventId, userId, qrCode, barcode, issuedAt, checkedIn, priceAtPurchase) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getEventId());
            ps.setInt(2, ticket.getUserId());
            ps.setString(3, ticket.getQrCode());
            ps.setString(4, ticket.getBarcode());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getIssuedAt()));
            ps.setBoolean(6, ticket.isCheckedIn());
            ps.setDouble(7, ticket.getPriceAtPurchase());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ticket.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error saving ticket", e);
        }
        return false;
    }

    @Override
    public boolean update(Ticket ticket) {
        String sql = "UPDATE Tickets SET eventId = ?, userId = ?, qrCode = ?, barcode = ?, issuedAt = ?, checkedIn = ?, priceAtPurchase = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ticket.getEventId());
            ps.setInt(2, ticket.getUserId());
            ps.setString(3, ticket.getQrCode());
            ps.setString(4, ticket.getBarcode());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getIssuedAt()));
            ps.setBoolean(6, ticket.isCheckedIn());
            ps.setDouble(7, ticket.getPriceAtPurchase());
            ps.setInt(8, ticket.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error updating ticket", e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Tickets WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error deleting ticket", e);
        }
        return false;
    }

    // ==== Additional DAO Methods ====

    public int getTotalTicketCount() {
        String sql = "SELECT COUNT(*) AS total FROM Tickets";
        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            LOGGER.error("Error getting total ticket count", e);
        }
        return 0;
    }

    public int getTicketCountByEvent(int eventId) {
        String sql = "SELECT COUNT(*) AS total FROM Tickets WHERE eventId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting ticket count by event", e);
        }
        return 0;
    }

    public List<Ticket> findByUserId(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE userId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractTicket(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding tickets by user id", e);
        }
        return tickets;
    }

    public List<Ticket> findTicketsByEventId(int eventId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE eventId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractTicket(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding tickets by event id", e);
        }
        return tickets;
    }

    public boolean deleteByEventId(int eventId) {
        String sql = "DELETE FROM Tickets WHERE eventId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting tickets by event id", e);
        }
        return false;
    }


    // ==== Utility Methods ====

    private Ticket extractTicket(ResultSet rs) throws SQLException {
        return ResultSetExtractor.extractTicket(rs);
    }
}

