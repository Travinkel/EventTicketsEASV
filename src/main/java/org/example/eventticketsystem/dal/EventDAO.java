package org.example.eventticketsystem.dal;

import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.models.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Injectable
public class EventDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDAO.class);

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events";

        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }

        } catch (SQLException e) {
            LOGGER.error("Failed to XYZ", e);
        }

        return events;
    }

    public boolean saveEvent(Event event) {
        String sql = "INSERT INTO Events (name, description, location, startTime, endTime, coordinatorId) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            stmt.setInt(6, event.getCoordinatorId());

            int rows = stmt.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            LOGGER.error("Failed to XYZ", e);
        }

        return false;
    }

    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getTimestamp("startTime").toLocalDateTime(),
                rs.getTimestamp("endTime").toLocalDateTime(),
                rs.getInt("coordinatorId"),
                rs.getInt("price"),
                rs.getBoolean("isPublic")
        );
    }

    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM Events WHERE id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Failed to XYZ", e);
            return false;
        }
    }

    public List<Event> getEventsByCoordinatorId(int coordinatorId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events WHERE coordinatorId = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, coordinatorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(extractEventFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Failed to XYZ", e);
        }

        return events;
    }

    public boolean updateEvent(Event event) {
        String sql = "UPDATE Events SET name=?, description=?, location=?, startTime=?, endTime=?, coordinatorId=? WHERE id=?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            stmt.setInt(6, event.getCoordinatorId());
            stmt.setInt(7, event.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Failed to XYZ", e);
            return false;
        }
    }

    public Optional<Event> getEventById(int eventId) {
        String sql = "SELECT * FROM Events WHERE id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractEventFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
