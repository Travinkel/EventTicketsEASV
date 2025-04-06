package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.dal.models.SpecialTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class SpecialTicketDAO implements GenericDAO<SpecialTicket> {

    private static final Logger logger = LoggerFactory.getLogger(SpecialTicketDAO.class);
    private final DBConnection dbConnection;

    public SpecialTicketDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==== CRUD ====

    @Override
    public List<SpecialTicket> findAll() {
        List<SpecialTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM SpecialTickets";
        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all special tickets", e);
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
                if (rs.next()) return Optional.of(extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding special ticket by id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(SpecialTicket ticket) {
        String sql = "INSERT INTO SpecialTickets (eventId, userId, qrCode, barcode, issuedAt, checkedIn, priceAtPurchase, specialFeature) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getEventId());
            ps.setInt(2, ticket.getUserId());
            ps.setString(3, ticket.getQrCode());
            ps.setString(4, ticket.getBarCode());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getIssuedAt()));
            ps.setBoolean(6, ticket.isCheckedIn());
            ps.setDouble(7, ticket.getPriceAtPurchase());
            ps.setString(8, ticket.getSpecialFeature());
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
        String sql = "UPDATE SpecialTickets SET eventId = ?, userId = ?, qrCode = ?, barcode = ?, issuedAt = ?, checkedIn = ?, priceAtPurchase = ?, specialFeature = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ticket.getEventId());
            ps.setInt(2, ticket.getUserId());
            ps.setString(3, ticket.getQrCode());
            ps.setString(4, ticket.getBarCode());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getIssuedAt()));
            ps.setBoolean(6, ticket.isCheckedIn());
            ps.setDouble(7, ticket.getPriceAtPurchase());
            ps.setString(8, ticket.getSpecialFeature());
            ps.setInt(9, ticket.getId());
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

    // ==== Additional DAO Methods ====

    public List<SpecialTicket> findByUserId(int userId) {
        List<SpecialTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM SpecialTickets WHERE userId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding special tickets by user id", e);
        }
        return tickets;
    }

    public List<SpecialTicket> findByEventId(int eventId) {
        List<SpecialTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM SpecialTickets WHERE eventId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(extractSpecialTicket(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding special tickets by event id", e);
        }
        return tickets;
    }

    // ==== Utility Methods ====

    private SpecialTicket extractSpecialTicket(ResultSet rs) throws SQLException {
        return new SpecialTicket(
                rs.getInt("id"),
                rs.getInt("eventId"),
                rs.getInt("userId"),
                rs.getString("qrCode"),
                rs.getString("barcode"),
                rs.getTimestamp("issuedAt").toLocalDateTime(),
                rs.getBoolean("checkedIn"),
                rs.getDouble("priceAtPurchase"),
                rs.getString("specialFeature")
        );
    }
}