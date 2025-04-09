package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.dal.models.UserEventRole;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Repository;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 📚 Repository for managing User-Event-Role relationships.
 * This class handles CRUD operations and additional queries for the `UserEventRoles` table.
 * <p>
 * 🧱 Design Pattern: Data Access Object (DAO)
 * <p>
 * 🔗 Dependencies:
 * - {@link DBConnection} for database connectivity
 * - {@link ResultSetExtractor} for mapping result sets to models
 * - {@link UserEventRole}, {@link Event}, {@link User} for domain models
 */
@Repository
@Singleton
@Scope("singleton")
public class UserEventRoleRepository implements IRepository<UserEventRole> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventRoleRepository.class);
    private final DBConnection dbConnection;

    /**
     * 🔧 Constructor for UserEventRoleRepository.
     *
     * @param dbConnection The database connection instance injected by the DI framework.
     */
    @Inject
    public UserEventRoleRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==== CRUD ====

    /**
     * 🔍 Fetches all user-event-role relationships from the database.
     * <p>
     * 📌 This method retrieves all rows from the `UserEventRoles` table and maps them to {@link UserEventRole} objects.
     *
     * @return A list of {@link UserEventRole} objects.
     */
    @Override
    public List<UserEventRole> findAll() {
        // 🔍 Log the start of the operation for debugging purposes
        LOGGER.debug("🔍 Executing findAll() to fetch all user-event roles");
        List<UserEventRole> roles = new ArrayList<>();
        String sql = "SELECT * FROM UserEventRoles";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(ResultSetExtractor.extractUserEventRole(rs));
            }
            LOGGER.info("✅ Retrieved {} user-event roles from the database", roles.size());
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding all user-event roles", e);
        }
        return roles;
    }

    /**
     * 🚫 Unsupported operation for composite keys.
     * <p>
     * 📌 This method is intentionally not implemented because the `UserEventRoles` table uses composite keys (userId, eventId).
     *
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public Optional<UserEventRole> findById(int id) {
        // 📌 Explain why this operation is not supported
        throw new UnsupportedOperationException("Use composite key (userId, eventId) instead.");
    }

    /**
     * ➕ Saves a new user-event-role relationship.
     * <p>
     * 📌 This method delegates to {@link #assignRole(int, int, String)} to insert a new record into the database.
     *
     * @param role The {@link UserEventRole} object to save.
     * @return True if the operation was successful, false otherwise.
     */
    @Override
    public boolean save(UserEventRole role) {
        // 📌 Use the assignRole method to handle the database insertion
        return assignRole(role.getUserId(), role.getEventId(), role.getRole());
    }

    /**
     * ♻️ Updates an existing user-event-role relationship.
     * <p>
     * 📌 Updates the `role` field for a specific user and event in the `UserEventRoles` table.
     *
     * @param role The {@link UserEventRole} object to update.
     * @return True if the update was successful, false otherwise.
     */
    @Override
    public boolean update(UserEventRole role) {
        String sql = "UPDATE UserEventRoles SET role = ? WHERE userId = ? AND eventId = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, role.getRole());
            ps.setInt(2, role.getUserId());
            ps.setInt(3, role.getEventId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Error updating user event role", e);
        }
        return false;
    }

    /**
     * 🚫 Unsupported operation for composite keys.
     *
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean delete(int id) {
        throw new UnsupportedOperationException("Delete must be called with both userId and eventId.");
    }

    // ==== Additional DAO Methods ====

    /**
     * ➕ Assigns a role to a user for a specific event.
     * <p>
     * 📌 Inserts a new record into the `UserEventRoles` table with the specified userId, eventId, and role.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @param role    The role to assign.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean assignRole(int userId, int eventId, String role) {
        // 📌 Convert the role to uppercase to ensure consistency in the database
        String sql = "INSERT INTO UserEventRoles (userId, eventId, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, role.toUpperCase());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Failed to assign role '{}' to user {} for event {}", role, userId, eventId, e);
            return false;
        }
    }

    /**
     * ➕ Assigns the "EVENTCOORDINATOR" role to a user for a specific event.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean assignCoordinatorToEvent(int userId, int eventId) {
        String sql = "INSERT INTO UserEventRoles (roleId, eventId, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, "EVENTCOORDINATOR");
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Error assigning coordinator to event", e);
        }
        return false;
    }

    /**
     * 🔍 Retrieves all events where the user is an "EVENTCOORDINATOR".
     * <p>
     * 📌 This method joins the `Events`, `UserEventRoles`, and `Roles` tables to fetch events where the user has the "EVENTCOORDINATOR" role.
     *
     * @param userId The ID of the user.
     * @return A list of {@link Event} objects.
     */
    public List<Event> getEventsByUserId(int userId) {
        // 📌 Use a multi-line SQL query for readability
        String sql = """
                SELECT e.* FROM Events e
                JOIN UserEventRoles uer ON uer.eventId = e.id
                WHERE uer.userId = ? AND uer.role = 'EVENTCOORDINATOR'
                """;
        List<Event> events = new ArrayList<>();
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("locationGuidance"),
                        rs.getTimestamp("startTime")
                                .toLocalDateTime(),
                        rs.getTimestamp("endTime")
                                .toLocalDateTime(),
                        rs.getDouble("price"),
                        rs.getInt("capacity"),
                        rs.getBoolean("isPublic")
                ));
            }
        } catch (SQLException e) {
            LOGGER.error("❌ Error getting events by user id", e);
        }
        return events;
    }

    /**
     * 🗑 Removes the "EVENTCOORDINATOR" role from a user for a specific event.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean removeCoordinatorFromEvent(int userId, int eventId) {
        String sql = "DELETE FROM UserEventRoles WHERE roleId = ? AND eventId = ? AND role = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, "EVENTCOORDINATOR");
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Error removing coordinator from event", e);
        }
        return false;
    }

    /**
     * 🗑 Removes a specific role from a user for a specific event.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @param role    The role to remove.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean removeRole(int userId, int eventId, String role) {
        String sql = "DELETE FROM UserEventRoles WHERE userId = ? AND eventId = ? AND role = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, role.toUpperCase());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Failed to remove role '{}' from user {} for event {}", role, userId, eventId, e);
            return false;
        }
    }

    /**
     * 🔍 Finds events associated with a user based on their role.
     *
     * @param userId The ID of the user.
     * @return A list of {@link Event} objects.
     */
    public List<Event> findEventsByUserId(int userId) {
        List<Event> events = new ArrayList<>();
        String sql = """
                SELECT e.* FROM Events e
                JOIN UserEventRoles uer ON e.id = uer.eventId
                WHERE uer.role = ?
                """;
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                events.add(ResultSetExtractor.extractEvent(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("❌ Failed to fetch events for user {}", userId, e);
        }
        return events;
    }

    /**
     * ➕ Assigns the "COORDINATOR" role to a user for a specific event.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean assignCoordinator(int userId, int eventId) {
        return assignRole(userId, eventId, "COORDINATOR");
    }

    /**
     * 🔍 Retrieves all coordinators for a specific event.
     *
     * @param eventId The ID of the event.
     * @return A list of {@link User} objects.
     */
    public List<User> getCoordinatorsForEvent(int eventId) {
        List<User> coordinators = new ArrayList<>();
        String sql = """
                SELECT u.* FROM Users u
                JOIN UserEventRoles ur ON u.id = ur.userId
                WHERE ur.eventId = ? AND ur.role = ''
                """;
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                coordinators.add(extractUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting coordinators for event", e);
        }
        return coordinators;
    }

    /**
     * 🔧 Extracts a {@link User} object from a {@link ResultSet}.
     * <p>
     * 📌 This helper method maps a single row of the `Users` table to a {@link User} object.
     *
     * @param rs The result set containing user data.
     * @return A {@link User} object.
     * @throws SQLException If an error occurs while accessing the result set.
     */
    private User extractUser(ResultSet rs) throws SQLException {
        // 📌 Map each column in the result set to the corresponding field in the User object
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("hashedPassword"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getTimestamp("createdAt")
                        .toLocalDateTime()
        );
    }

    /**
     * 🔍 Retrieves all event IDs where the user is a coordinator.
     * <p>
     * 📌 This method fetches event IDs from the `UserEventRoles` table where the user has the "COORDINATOR" role.
     *
     * @param userId The ID of the user.
     * @return A list of event IDs.
     */
    public List<Integer> getEventIdsForCoordinator(int userId) {
        List<Integer> eventIds = new ArrayList<>();
        String sql = "SELECT eventId FROM UserEventRoles WHERE userId = ? AND role = ''";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                eventIds.add(rs.getInt("eventId"));
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting event ids for coordinator", e);
        }
        return eventIds;
    }
}
