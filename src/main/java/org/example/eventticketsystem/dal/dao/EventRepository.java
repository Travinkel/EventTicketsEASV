// EventRepository.java
package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Repository;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing CRUD operations on Event entities.
 * This class is a singleton and managed by the DI framework.
 */
@Repository
@Singleton
@Scope("singleton")
public class EventRepository implements IRepository<Event> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventRepository.class);
    private final DBConnection dbConnection;

    /**
     * Constructor for EventRepository.
     * The DI framework injects the DBConnection dependency.
     *
     * @param dbConnection The database connection instance.
     */
    @Inject
    public EventRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==== CRUD ====

    /**
     * Retrieves all events from the database.
     *
     * @return A list of all events.
     */
    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events";

        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            LOGGER.debug("📦 Executing SQL query: {}", sql);
            while (rs.next()) {
                events.add(ResultSetExtractor.extractEvent(rs));
            }
            LOGGER.info("✅ Retrieved {} events from the database.", events.size());

        } catch (SQLException e) {
            LOGGER.error("❌ Error finding all events. SQL: {}", sql, e);
        }

        return events;
    }

    /**
     * Finds an event by its ID.
     *
     * @param id The ID of the event.
     * @return An Optional containing the event if found, or empty if not.
     */
    @Override
    public Optional<Event> findById(int id) {
        String sql = "SELECT * FROM Events WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            LOGGER.debug("📦 Executing SQL query: {} with id={}", sql, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("✅ Event with ID {} found.", id);
                    return Optional.of(ResultSetExtractor.extractEvent(rs));
                }
            }
            LOGGER.warn("⚠️ No event found with ID {}", id);
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding event by ID: {}", id, e);
        }
        return Optional.empty();
    }

    /**
     * Saves a new event to the database.
     *
     * @param event The event to save.
     * @return True if the event was saved successfully, false otherwise.
     */
    @Override
    public boolean save(Event event) {
        String sql =
                "INSERT INTO Events (title, description, locationGuidance, startTime, endTime, price, capacity, isPublic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getLocationGuidance());
            ps.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            ps.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            ps.setDouble(6, event.getPrice());
            ps.setInt(7, event.getCapacity());
            ps.setBoolean(8, event.isPublic());

            LOGGER.debug("📦 Executing SQL insert: {}", sql);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                LOGGER.warn("⚠️ No rows affected while saving event: {}", event.getTitle());
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    event.setId(keys.getInt(1));
                    LOGGER.info("➕ Event '{}' saved with ID {}", event.getTitle(), event.getId());
                }
            }
            return true;
        } catch (SQLException e) {
            LOGGER.error("❌ Error saving event: {}", event.getTitle(), e);
        }
        return false;
    }

    /**
     * Updates an existing event in the database.
     *
     * @param event The event to update.
     * @return True if the event was updated successfully, false otherwise.
     */
    @Override
    public boolean update(Event event) {
        String sql =
                "UPDATE Events SET title = ?, description = ?, locationGuidance = ?, startTime = ?, endTime = ?, price = ?, capacity = ?, isPublic = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getLocationGuidance());
            ps.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            ps.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            ps.setDouble(6, event.getPrice());
            ps.setInt(7, event.getCapacity());
            ps.setBoolean(8, event.isPublic());
            ps.setInt(9, event.getId());

            LOGGER.debug("📦 Executing SQL update: {} for event ID {}", sql, event.getId());
            if (ps.executeUpdate() == 1) {
                LOGGER.info("♻️ Event '{}' updated successfully.", event.getTitle());
                return true;
            }
            LOGGER.warn("⚠️ No rows affected while updating event ID {}", event.getId());
        } catch (SQLException e) {
            LOGGER.error("❌ Error updating event ID {}", event.getId(), e);
        }
        return false;
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId The ID of the event to delete.
     * @return True if the event was deleted successfully, false otherwise.
     */
    @Override
    public boolean delete(int eventId) {
        String sql = "DELETE FROM Events WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            LOGGER.debug("📦 Executing SQL delete: {} for event ID {}", sql, eventId);
            if (ps.executeUpdate() > 0) {
                LOGGER.info("🗑 Event with ID {} deleted successfully.", eventId);
                return true;
            }
            LOGGER.warn("⚠️ No rows affected while deleting event ID {}", eventId);
        } catch (SQLException e) {
            LOGGER.error("❌ Error deleting event with ID {}", eventId, e);
        }
        return false;
    }

    // ==== Additional Methods ====

    /**
     * Finds events by the coordinator's ID.
     *
     * @param coordinatorId The ID of the coordinator.
     * @return A list of events managed by the coordinator.
     */
    public List<Event> findEventsByCoordinatorId(int coordinatorId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events WHERE coordinatorId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, coordinatorId);
            LOGGER.debug("📦 Executing SQL query: {} with coordinatorId={}", sql, coordinatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(ResultSetExtractor.extractEvent(rs));
                }
            }
            LOGGER.info("✅ Retrieved {} events for coordinator ID {}", events.size(), coordinatorId);
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding events by coordinator ID {}", coordinatorId, e);
        }
        return events;
    }
}
