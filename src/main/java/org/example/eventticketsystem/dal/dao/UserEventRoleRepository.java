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
 * Repository for managing User-Event-Role relationships.
 * This class is managed by the DI framework.
 */
@Repository
@Singleton
@Scope("singleton")
public class UserEventRoleRepository implements IRepository<UserEventRole> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventRoleRepository.class);
    private final DBConnection dbConnection;

    /**
     * Constructor for UserEventRoleRepository.
     * The DI framework injects the DBConnection dependency.
     *
     * @param dbConnection The database connection instance.
     */
    @Inject
    public UserEventRoleRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==== CRUD ====

    @Override
    public List<UserEventRole> findAll() {
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

    @Override
    public Optional<UserEventRole> findById(int id) {
        throw new UnsupportedOperationException("Use composite key (userId, eventId) instead.");
    }

    @Override
    public boolean save(UserEventRole role) {
        return assignRole(role.getUserId(), role.getEventId(), role.getRole());
    }

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

    @Override
    public boolean delete(int id) {
        throw new UnsupportedOperationException("Delete must be called with both userId and eventId.");
    }

    // ==== Additional DAO Methods ====

    public boolean assignRole(int userId, int eventId, String role) {
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

    public boolean assignCoordinatorToEvent(int userId, int eventId) {
        String sql = "INSERT INTO UserEventRoles (roleId, eventId, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, "COORDINATOR");
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Error assigning coordinator to event", e);
        }
        return false;
    }

    public List<Event> getEventsByUserId(int userId) {
        List<Event> events = new ArrayList<>();
        String sql = """
                SELECT e.* FROM Events e
                JOIN UserEventRoles uer ON e.id = uer.eventId
                WHERE uer.role = ? AND uer.role = 'COORDINATOR'
                """;
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

    public boolean removeCoordinatorFromEvent(int userId, int eventId) {
        String sql = "DELETE FROM UserEventRoles WHERE roleId = ? AND eventId = ? AND role = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, "COORDINATOR");
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Error removing coordinator from event", e);
        }
        return false;
    }

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

    public boolean assignCoordinator(int userId, int eventId) {
        return assignRole(userId, eventId, "COORDINATOR");
    }

    public List<User> getCoordinatorsForEvent(int eventId) {
        List<User> coordinators = new ArrayList<>();
        String sql = """
                SELECT u.* FROM Users u
                JOIN UserEventRoles ur ON u.id = ur.userId
                WHERE ur.eventId = ? AND ur.role = 'COORDINATOR'
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

    private User extractUser(ResultSet rs) throws SQLException {
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

    public List<Integer> getEventIdsForCoordinator(int userId) {
        List<Integer> eventIds = new ArrayList<>();
        String sql = "SELECT eventId FROM UserEventRoles WHERE userId = ? AND role = 'COORDINATOR'";
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
