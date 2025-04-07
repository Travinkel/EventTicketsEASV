// EventRepository.java
package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class EventRepository implements GenericRepository<Event> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventRepository.class);
    private final DBConnection dbConnection;

    public EventRepository() throws SQLException {
        this.dbConnection = DBConnection.getInstance();
        if (this.dbConnection == null || this.dbConnection.getConnection().isClosed()) {
            throw new IllegalStateException("❌ Cannot initialize EventRepository: no DB connection");
        }
    }

    // ==== CRUD ====

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events";

        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(ResultSetExtractor.extractEvent(rs));
            }

        } catch (SQLException e) {
            LOGGER.error("Error finding all events", e);
        }

        return events;
    }

    @Override
    public Optional<Event> findById(int id) {
        String sql = "SELECT * FROM Events WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(ResultSetExtractor.extractEvent(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding event by id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Event event) {
        String sql = "INSERT INTO Events (title, description, locationGuidance, startTime, endTime, price, capacity, isPublic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) event.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error saving event", e);
        }
        return false;
    }

    @Override
    public boolean update(Event event) {
        String sql = "UPDATE Events SET title = ?, description = ?, locationGuidance = ?, startTime = ?, endTime = ?, price = ?, capacity = ?, isPublic = ? WHERE id = ?";
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
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error updating event", e);
        }
        return false;
    }

    @Override
    public boolean delete(int eventId) {
        String sql = "DELETE FROM Events WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting event with ID: {}", eventId, e);
            return false;
        }
    }

    // ==== Additional Methods ====

    public List<Event> findEventsByCoordinatorId(int coordinatorId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events WHERE coordinatorId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, coordinatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(ResultSetExtractor.extractEvent(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding events by coordinator id", e);
        }
        return events;
    }

    // ==== Utility Methods ====

    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("locationGuidance"),
                rs.getTimestamp("startTime").toLocalDateTime(),
                rs.getTimestamp("endTime").toLocalDateTime(),
                rs.getDouble("price"),
                rs.getInt("capacity"),
                rs.getBoolean("isPublic")
        );
    }
}
