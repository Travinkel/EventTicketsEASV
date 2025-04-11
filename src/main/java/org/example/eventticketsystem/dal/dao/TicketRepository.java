/**
 * 📚 Repository for managing Ticket entities and their relationships with events and users.
 * This class provides CRUD operations and additional queries for the `Tickets` table.
 * It supports operations related to ticket management, such as assignment, retrieval, and deletion.
 * <p>
 * 🧱 Design Pattern: Data Access Object (DAO)
 * - Encapsulates database access logic for the `Tickets` table.
 * - Promotes separation of concerns by isolating persistence logic from business logic.
 * <p>
 * 🔗 Dependencies:
 * - {@link org.example.eventticketsystem.dal.connection.DBConnection} for establishing database connections.
 * - {@link org.example.eventticketsystem.dal.helpers.ResultSetExtractor} for mapping SQL result sets to domain models.
 * - {@link org.example.eventticketsystem.dal.models.Ticket} for representing ticket entities.
 * - {@link org.example.eventticketsystem.dal.models.Event} and {@link org.example.eventticketsystem.dal.models.User} for related domain models.
 * <p>
 * 🧩 Responsibilities:
 * - Perform CRUD operations on the `Tickets` table.
 * - Support ticket-related queries, such as finding tickets by user or event.
 * - Facilitate ticket assignment and management for events and users.
 * - Ensure transactional integrity and handle SQL exceptions gracefully.
 * <p>
 * 🛠️ Usage:
 * - This repository is managed as a singleton by the DI framework.
 * - Use this class in service layers to interact with ticket-related data.
 */

package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.example.eventticketsystem.dal.models.Ticket;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Repository;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing CRUD operations on Ticket entities.
 * This class is managed by the DI framework.
 */
@Repository
@Singleton
@Scope("singleton")
public class TicketRepository implements IRepository<Ticket> {

    private static final Logger
            LOGGER =
            LoggerFactory.getLogger(TicketRepository.class);
    private final DBConnection
            dbConnection;

    /**
     * Constructor for TicketRepository.
     * The DI framework injects the DBConnection dependency.
     *
     * @param dbConnection The database connection instance.
     */
    @Inject
    public TicketRepository(DBConnection dbConnection) {
        this.dbConnection =
                dbConnection;
    }

    // ==== CRUD ====

    @Override
    public List<Ticket> findAll() {
        LOGGER.debug("🔍 Executing findAll() to fetch all tickets");
        List<Ticket>
                tickets =
                new ArrayList<>();
        String
                sql =
                "SELECT * FROM Tickets";
        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(ResultSetExtractor.extractTicket(rs));
            }
            LOGGER.info("✅ Retrieved {} tickets from the database",
                    tickets.size());
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding all tickets",
                    e);
        }
        return tickets;
    }

    @Override
    public Optional<Ticket> findById(int id) {
        LOGGER.debug("🔍 Executing findById() for ticket ID: {}",
                id);
        String
                sql =
                "SELECT * FROM Tickets WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,
                    id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("✅ Ticket with ID {} found",
                            id);
                    return Optional.of(ResultSetExtractor.extractTicket(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding ticket by ID: {}",
                    id,
                    e);
        }
        LOGGER.warn("⚠️ No ticket found with ID: {}",
                id);
        return Optional.empty();
    }

    @Override
    public boolean save(Ticket ticket) {
        LOGGER.debug("🔍 Executing save() for ticket: {}",
                ticket);

        if (ticket.getIssuedAt() ==
            null) {
            LOGGER.warn("issuedAt is null, setting it to current time.");
            ticket.setIssuedAt(LocalDateTime.now());
        }

        String
                sql =
                "INSERT INTO Tickets (eventId, userId, qrCode, barcode, issuedAt, checkedIn, priceAtPurchase) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,
                    ticket.getEventId());
            ps.setInt(2,
                    ticket.getUserId());
            ps.setString(3,
                    ticket.getQrCode());
            ps.setString(4,
                    ticket.getBarcode());
            ps.setTimestamp(5,
                    Timestamp.valueOf(ticket.getIssuedAt()));
            ps.setBoolean(6,
                    ticket.isCheckedIn());
            ps.setDouble(7,
                    ticket.getPriceAtPurchase());
            int
                    rows =
                    ps.executeUpdate();
            if (rows ==
                0) {
                LOGGER.warn("⚠️ No rows affected while saving ticket: {}",
                        ticket);
                return false;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    ticket.setId(keys.getInt(1));
                    LOGGER.info("➕ Ticket saved with ID: {}",
                            ticket.getId());
                }
            }
            return true;
        } catch (SQLException e) {
            LOGGER.error("❌ Error saving ticket: {}",
                    ticket,
                    e);
        }
        return false;
    }

    @Override
    public boolean update(Ticket ticket) {
        LOGGER.debug("🔍 Executing update() for ticket ID: {}",
                ticket.getId());
        String
                sql =
                "UPDATE Tickets SET eventId = ?, userId = ?, qrCode = ?, barcode = ?, issuedAt = ?, checkedIn = ?, priceAtPurchase = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,
                    ticket.getEventId());
            ps.setInt(2,
                    ticket.getUserId());
            ps.setString(3,
                    ticket.getQrCode());
            ps.setString(4,
                    ticket.getBarcode());
            ps.setTimestamp(5,
                    Timestamp.valueOf(ticket.getIssuedAt()));
            ps.setBoolean(6,
                    ticket.isCheckedIn());
            ps.setDouble(7,
                    ticket.getPriceAtPurchase());
            ps.setInt(8,
                    ticket.getId());
            if (ps.executeUpdate() ==
                1) {
                LOGGER.info("♻️ Ticket with ID {} updated successfully",
                        ticket.getId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error("❌ Error updating ticket ID: {}",
                    ticket.getId(),
                    e);
        }
        LOGGER.warn("⚠️ No rows affected while updating ticket ID: {}",
                ticket.getId());
        return false;
    }

    @Override
    public boolean delete(int id) {
        LOGGER.debug("🔍 Executing delete() for ticket ID: {}",
                id);
        String
                sql =
                "DELETE FROM Tickets WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (ps.executeUpdate() ==
                1) {
                LOGGER.info("🗑 Ticket with ID {} deleted successfully",
                        id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error("❌ Error deleting ticket ID: {}",
                    id,
                    e);
        }
        LOGGER.warn("⚠️ No rows affected while deleting ticket ID: {}",
                id);
        return false;
    }

    // ==== Additional DAO Methods ====

    public int getTotalTicketCount() {
        String
                sql =
                "SELECT COUNT(*) AS total FROM Tickets";
        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            LOGGER.error("Error getting total ticket count",
                    e);
        }
        return 0;
    }

    public int getTicketCountByEvent(int eventId) {
        String
                sql =
                "SELECT COUNT(*) AS total FROM Tickets WHERE eventId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,
                    eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting ticket count by event",
                    e);
        }
        return 0;
    }

    public List<Ticket> findByUserId(int userId) {
        List<Ticket>
                tickets =
                new ArrayList<>();
        String
                sql =
                "SELECT * FROM Tickets WHERE userId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,
                    userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractTicket(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding tickets by user id",
                    e);
        }
        return tickets;
    }

    public List<Ticket> findTicketsByEventId(int eventId) {
        List<Ticket>
                tickets =
                new ArrayList<>();
        String
                sql =
                "SELECT * FROM Tickets WHERE eventId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,
                    eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractTicket(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding tickets by event id",
                    e);
        }
        return tickets;
    }

    public boolean deleteByEventId(int eventId) {
        String
                sql =
                "DELETE FROM Tickets WHERE eventId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,
                    eventId);
            return ps.executeUpdate() >
                   0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting tickets by event id",
                    e);
        }
        return false;
    }


    // ==== Utility Methods ====

    private Ticket extractTicket(ResultSet rs) throws SQLException {
        return ResultSetExtractor.extractTicket(rs);
    }
}
