package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.example.eventticketsystem.dal.models.SpecialTicket;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing CRUD operations on SpecialTicket entities.
 * This class is a singleton and managed by the DI framework.
 */
@Injectable
@Singleton
@Scope("singleton")
public class SpecialTicketRepository implements IRepository<SpecialTicket> {

    private static final Logger logger = LoggerFactory.getLogger(SpecialTicketRepository.class);
    private final DBConnection dbConnection;

    /**
     * Constructor for SpecialTicketRepository.
     * The DI framework injects the DBConnection dependency.
     *
     * @param dbConnection The database connection instance.
     */
    @Inject
    public SpecialTicketRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==== CRUD ====

    /**
     * Retrieves all special tickets from the database.
     *
     * @return A list of all special tickets.
     */
    @Override
    public List<SpecialTicket> findAll() {
        String sql = "SELECT * FROM SpecialTickets";
        List<SpecialTicket> tickets = new ArrayList<>();
        logger.debug("📦 Executing query to fetch all special tickets: {}", sql);
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
            logger.info("✅ Retrieved {} special tickets from the database.", tickets.size());
        } catch (SQLException e) {
            logger.error("❌ Error fetching all special tickets", e);
        }
        return tickets;
    }

    /**
     * Finds a special ticket by its ID.
     *
     * @param id The ID of the special ticket.
     * @return An Optional containing the special ticket if found, or empty if not.
     */
    @Override
    public Optional<SpecialTicket> findById(int id) {
        String sql = "SELECT * FROM SpecialTickets WHERE id = ?";
        logger.debug("📦 Executing query to find special ticket by ID: {}", id);
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("✅ Found special ticket with ID: {}", id);
                    return Optional.of(ResultSetExtractor.extractSpecialTicket(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("❌ Error finding special ticket by ID: {}", id, e);
        }
        logger.warn("⚠️ No special ticket found with ID: {}", id);
        return Optional.empty();
    }

    /**
     * Saves a new special ticket to the database.
     *
     * @param ticket The special ticket to save.
     * @return True if the special ticket was saved successfully, false otherwise.
     */
    @Override
    public boolean save(SpecialTicket ticket) {
        String sql =
                "INSERT INTO SpecialTickets (event_id, user_id, type, issued_by, created_at, qrCode, barcode) VALUES (?, ?, ?, ?, ?, ?, ?)";
        logger.debug("📦 Preparing to save special ticket: {}", ticket);
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
            if (rows == 0) {
                logger.warn("⚠️ No rows affected while saving special ticket: {}", ticket);
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    ticket.setId(keys.getInt(1));
                    logger.info("➕ Successfully saved special ticket with ID: {}", ticket.getId());
                }
            }
            return true;
        } catch (SQLException e) {
            logger.error("❌ Error saving special ticket: {}", ticket, e);
        }
        return false;
    }

    /**
     * Updates an existing special ticket in the database.
     *
     * @param ticket The special ticket to update.
     * @return True if the special ticket was updated successfully, false otherwise.
     */
    @Override
    public boolean update(SpecialTicket ticket) {
        String sql =
                "UPDATE SpecialTickets SET event_id = ?, user_id = ?, type = ?, issued_by = ?, created_at = ?, qrCode = ?, barcode = ? WHERE id = ?";
        logger.debug("♻️ Preparing to update special ticket: {}", ticket);
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
            if (ps.executeUpdate() == 1) {
                logger.info("♻️ Successfully updated special ticket with ID: {}", ticket.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("❌ Error updating special ticket: {}", ticket, e);
        }
        logger.warn("⚠️ No rows affected while updating special ticket with ID: {}", ticket.getId());
        return false;
    }

    /**
     * Deletes a special ticket by its ID.
     *
     * @param id The ID of the special ticket to delete.
     * @return True if the special ticket was deleted successfully, false otherwise.
     */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM SpecialTickets WHERE id = ?";
        logger.debug("🗑 Preparing to delete special ticket with ID: {}", id);
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 1) {
                logger.info("🗑 Successfully deleted special ticket with ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("❌ Error deleting special ticket with ID: {}", id, e);
        }
        logger.warn("⚠️ No rows affected while deleting special ticket with ID: {}", id);
        return false;
    }

    // ==== Additional Methods ====

    /**
     * Assigns a special ticket to a user.
     *
     * @param ticketId The ID of the ticket.
     * @param userId   The ID of the user.
     * @return True if the ticket was assigned successfully, false otherwise.
     */
    public boolean assignToUser(int ticketId, int userId) {
        String sql = "UPDATE SpecialTickets SET user_id = ? WHERE id = ?";
        logger.debug("♻️ Assigning special ticket ID {} to user ID {}", ticketId, userId);
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, ticketId);
            if (ps.executeUpdate() == 1) {
                logger.info("✅ Successfully assigned special ticket ID {} to user ID {}", ticketId, userId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("❌ Error assigning special ticket ID {} to user ID {}", ticketId, userId, e);
        }
        logger.warn("⚠️ No rows affected while assigning special ticket ID {} to user ID {}", ticketId, userId);
        return false;
    }

    /**
     * Finds special tickets by user ID.
     *
     * @param userId The ID of the user.
     * @return A list of special tickets assigned to the user.
     */
    public List<SpecialTicket> findByUserId(int userId) {
        String sql = "SELECT * FROM SpecialTickets WHERE user_id = ?";
        logger.debug("📦 Executing query to find special tickets by user ID: {}", userId);
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
            logger.info("✅ Retrieved {} special tickets for user ID: {}", tickets.size(), userId);
        } catch (SQLException e) {
            logger.error("❌ Error finding special tickets by user ID: {}", userId, e);
        }
        return tickets;
    }

    /**
     * Finds special tickets by event ID.
     *
     * @param eventId The ID of the event.
     * @return A list of special tickets for the event.
     */
    public List<SpecialTicket> findByEventId(int eventId) {
        String sql = "SELECT * FROM SpecialTickets WHERE event_id = ?";
        logger.debug("📦 Executing query to find special tickets by event ID: {}", eventId);
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
            logger.info("✅ Retrieved {} special tickets for event ID: {}", tickets.size(), eventId);
        } catch (SQLException e) {
            logger.error("❌ Error finding special tickets by event ID: {}", eventId, e);
        }
        return tickets;
    }

    public List<SpecialTicket> findAllByIssuedBy(int issuedBy) {
        String sql = "SELECT * FROM SpecialTickets WHERE issued_by = ?";
        logger.debug("📦 Executing query to find special tickets by issuer ID: {}", issuedBy);
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, issuedBy);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
            logger.info("✅ Retrieved {} special tickets issued by ID: {}", tickets.size(), issuedBy);
        } catch (SQLException e) {
            logger.error("❌ Error finding special tickets by issuer ID: {}", issuedBy, e);
        }
        return tickets;
    }

    public List<SpecialTicket> findUnassignedByEvent(int eventId) {
        String sql = "SELECT * FROM SpecialTickets WHERE event_id = ? AND user_id IS NULL";
        logger.debug("📦 Executing query to find unassigned special tickets by event ID: {}", eventId);
        List<SpecialTicket> tickets = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tickets.add(ResultSetExtractor.extractSpecialTicket(rs));
            }
            logger.info("✅ Retrieved {} unassigned special tickets for event ID: {}", tickets.size(), eventId);
        } catch (SQLException e) {
            logger.error("❌ Error finding unassigned special tickets by event ID: {}", eventId, e);
        }
        return tickets;
    }
}
