/**
 * 📚 Repository for managing User-Event-Role relationships.
 * This class handles CRUD operations and additional queries for the UserEventRoles table.
 * <p>
 * 🧱 Design Pattern: Data Access Object (DAO)
 * <p>
 * 🔗 Dependencies:
 * {@link org.example.eventticketsystem.dal.connection.DBConnection} for database connectivity
 * {@link ResultSetExtractor} for mapping result sets to models
 * {@link org.example.eventticketsystem.dal.models.UserEventRole}, {@link Event}, {@link org.example.eventticketsystem.dal.models.User} for domain models
 */

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
     * 🔍 Retrieves all events from the database.
     * <p>
     * 📌 This method fetches all rows from the `Events` table and maps them to {@link Event} objects.
     *
     * @return A list of {@link Event} objects.
     */
    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events";

        LOGGER.debug("📦 Executing SQL query to fetch all events: {}", sql);

        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // 📌 Iterate through the result set and map each row to an Event object
            while (rs.next()) {
                events.add(ResultSetExtractor.extractEvent(rs));
            }

            LOGGER.info("✅ Successfully retrieved {} events from the database.", events.size());

        } catch (SQLException e) {
            LOGGER.error("❌ Error retrieving all events from the database.", e);
        }

        return events;
    }

    /**
     * 🔍 Finds an event by its ID.
     * <p>
     * 📌 This method retrieves a single row from the `Events` table based on the event ID.
     *
     * @param id The ID of the event to find.
     * @return An {@link Optional} containing the event if found, or empty if not found.
     */
    @Override
    public Optional<Event> findById(int id) {
        String sql = "SELECT * FROM Events WHERE id = ?";

        LOGGER.debug("📦 Executing SQL query to find event by ID: {}", id);

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

            LOGGER.warn("⚠️ No event found with ID: {}", id);

        } catch (SQLException e) {
            LOGGER.error("❌ Error finding event by ID: {}", id, e);
        }

        return Optional.empty();
    }

    /**
     * ➕ Saves a new event to the database.
     * <p>
     * 📌 Inserts a new record into the `Events` table and sets the generated ID on the {@link Event} object.
     *
     * @param event The {@link Event} object to save.
     * @return True if the operation was successful, false otherwise.
     */
    @Override
    public boolean save(Event event) {
        String sql = """
                INSERT INTO Events (title, description, locationGuidance, startTime, endTime, price, capacity, isPublic)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        LOGGER.debug("📦 Preparing to save event: {}", event);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 📌 Set parameters for the prepared statement
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

            // 📌 Retrieve the generated ID for the new event
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    event.setId(keys.getInt(1));
                    LOGGER.info("➕ Event '{}' saved with ID: {}", event.getTitle(), event.getId());
                }
            }

            return true;

        } catch (SQLException e) {
            LOGGER.error("❌ Error saving event: {}", event.getTitle(), e);
        }

        return false;
    }

    /**
     * ♻️ Updates an existing event in the database.
     * <p>
     * 📌 Updates the fields of an event in the `Events` table based on the event's ID.
     *
     * @param event The {@link Event} object to update.
     * @return True if the update was successful, false otherwise.
     */
    @Override
    public boolean update(Event event) {
        String sql = """
                UPDATE Events
                SET title = ?, description = ?, locationGuidance = ?, startTime = ?, endTime = ?, price = ?, capacity = ?, isPublic = ?
                WHERE id = ?
                """;

        LOGGER.debug("♻️ Preparing to update event: {}", event);

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

            boolean success = ps.executeUpdate() == 1;
            if (success) {
                LOGGER.info("♻️ Event '{}' successfully updated.", event.getTitle());
            } else {
                LOGGER.warn("⚠️ No rows affected while updating event with ID: {}", event.getId());
            }

            return success;

        } catch (SQLException e) {
            LOGGER.error("❌ Error updating event with ID: {}", event.getId(), e);
        }

        return false;
    }

    /**
     * 🗑 Deletes an event by its ID.
     * <p>
     * 📌 Deletes the event from the `Events` table based on the event's ID.
     *
     * @param eventId The ID of the event to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    @Override
    public boolean delete(int eventId) {
        String sql = "DELETE FROM Events WHERE id = ?";

        LOGGER.debug("🗑 Preparing to delete event with ID: {}", eventId);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                LOGGER.info("🗑 Event with ID {} successfully deleted.", eventId);
            } else {
                LOGGER.warn("⚠️ No rows affected while deleting event with ID: {}", eventId);
            }

            return success;

        } catch (SQLException e) {
            LOGGER.error("❌ Error deleting event with ID: {}", eventId, e);
        }

        return false;
    }

    // ==== Additional Methods ====

    /**
     * 🔍 Finds events by the coordinator's ID.
     * <p>
     * 📌 Retrieves all rows from the `Events` table where the `coordinatorId` matches the given ID.
     *
     * @param coordinatorId The ID of the coordinator.
     * @return A list of {@link Event} objects managed by the coordinator.
     */
    public List<Event> findEventsByCoordinatorId(int coordinatorId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events WHERE coordinatorId = ?";

        LOGGER.debug("📦 Executing SQL query to find events by coordinator ID: {}", coordinatorId);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, coordinatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(ResultSetExtractor.extractEvent(rs));
                }
            }

            LOGGER.info("✅ Successfully retrieved {} events for coordinator ID: {}", events.size(), coordinatorId);

        } catch (SQLException e) {
            LOGGER.error("❌ Error finding events by coordinator ID: {}", coordinatorId, e);
        }

        return events;
    }
}
