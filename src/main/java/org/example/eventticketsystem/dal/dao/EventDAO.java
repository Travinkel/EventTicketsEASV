// EventDAO.java
package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.dal.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class EventDAO implements GenericDAO<Event> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDAO.class);
    private final DBConnection dbConnection;

    public EventDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events";

        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }

        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve events", e);
        }

        return events;
    }

    @Override
    public boolean save(Event event) {
        String sql = "INSERT INTO Events (title, description, location, startTime, endTime, price, capacity, isPublic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            stmt.setDouble(6, event.getPrice());
            stmt.setInt(7, event.getCapacity());
            stmt.setBoolean(8, event.isPublic());

            int rows = stmt.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            LOGGER.error("Failed to save event", e);
            return false;
        }
    }

    @Override
    public boolean delete(int eventId) {
        String sql = "DELETE FROM Events WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Failed to delete event with ID: {}", eventId, e);
            return false;
        }
    }

    @Override
    public boolean update(Event event) {
        String sql = "UPDATE Events SET title = ?, description = ?, location = ?, startTime = ?, endTime = ?, price = ?, capacity = ?, isPublic = ? WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            stmt.setDouble(6, event.getPrice());
            stmt.setInt(7, event.getCapacity());
            stmt.setBoolean(8, event.isPublic());
            stmt.setInt(9, event.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Failed to update event with ID: {}", event.getId(), e);
            return false;
        }
    }

    @Override
    public Optional<Event> findById(int eventId) {
        String sql = "SELECT * FROM Events WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractEventFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Failed to find event with ID: {}", eventId, e);
        }

        return Optional.empty();
    }

    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
        return new Event(
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
    }
}