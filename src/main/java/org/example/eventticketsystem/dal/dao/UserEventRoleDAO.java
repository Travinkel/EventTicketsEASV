package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.dal.models.UserEventRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class UserEventRoleDAO implements GenericDAO<UserEventRole> {

    private final Connection connection;

    public UserEventRoleDAO() throws SQLException {
        this.connection = DBConnection.getInstance().getConnection();
        if (this.connection == null) {
            throw new IllegalStateException("❌ Cannot initialize UserEventRoleDAO: no DB connection");
        }
    }

    public boolean assignCoordinatorToEvent(int userId, int eventId) {
        String sql = "INSERT INTO UserEventRoles (user_Id, event_Id, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, "COORDINATOR");
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging
        }
        return false;
    }

    public List<Event> getEventsByUserId(int userId) {
        List<Event> events = new ArrayList<>();

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT e.* FROM Events e " +
                             "JOIN UserEventRoles uer ON e.id = uer.event_id " +
                             "WHERE uer.user_id = ? AND uer.role = 'Event Coordinator'")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("location"),
                        rs.getTimestamp("startTime").toLocalDateTime(),
                        rs.getTimestamp("endTime").toLocalDateTime(),
                        rs.getDouble("price"),
                        rs.getInt("capacity"),
                        rs.getBoolean("isPublic")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }


    public boolean removeCoordinatorFromEvent(int userId, int eventId) {
        String sql = "DELETE FROM UserEventRoles WHERE user_Id = ? AND event_Id = ? AND role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, "COORDINATOR");
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging
        }
        return false;
    }

    public List<User> getCoordinatorsForEvent(int eventId) {
        List<User> coordinators = new ArrayList<>();
        String sql = """
                SELECT u.* FROM Users u
                JOIN UserEventRoles ur ON u.id = ur.user_Id
                WHERE ur.event_id = ? AND ur.role = 'COORDINATOR'
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                coordinators.add(extractUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging
        }
        return coordinators;
    }

    public List<Integer> getEventIdsForCoordinator(int userId) {
        List<Integer> eventIds = new ArrayList<>();
        String sql = "SELECT event_Id FROM UserEventRoles WHERE user_Id = ? AND role = 'COORDINATOR'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                eventIds.add(rs.getInt("eventId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventIds;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("hashedPassword"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }

    public List<String> getRolesForUser(int userId) {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT role_name FROM UserRoles WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roles;
    }



    @Override
    public List<UserEventRole> findAll() {
        List<UserEventRole> roles = new ArrayList<>();
        String sql = "SELECT * FROM UserEventRoles";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(new UserEventRole(
                        rs.getInt("userId"),
                        rs.getInt("eventId"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public Optional<UserEventRole> findById(int id) {
        // ❗ This table has a composite key, so we can't find by single int ID
        throw new UnsupportedOperationException("Use a composite key (userId, eventId) instead.");
    }

    @Override
    public boolean save(UserEventRole role) {
        String sql = "INSERT INTO UserEventRoles (user_Id, event_Id, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, role.getUserId());
            ps.setInt(2, role.getEventId());
            ps.setString(3, role.getRole());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(UserEventRole role) {
        String sql = "UPDATE UserEventRoles SET role = ? WHERE user_Id = ? AND event_Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role.getRole());
            ps.setInt(2, role.getUserId());
            ps.setInt(3, role.getEventId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        // ❗ Not meaningful in composite-key join table — we throw
        throw new UnsupportedOperationException("Delete must be called with both userId and eventId.");
    }
}
