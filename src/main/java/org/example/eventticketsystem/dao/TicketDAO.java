package org.example.eventticketsystem.dao;

import org.example.eventticketsystem.database.DBConnection;
import org.example.eventticketsystem.models.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Events";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public boolean saveEvent(Event event) {
        String sql = "INSERT INTO Events (title, description, location, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getDate()));
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM Events WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Event extractFromResultSet(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getTimestamp("date").toLocalDateTime()
        );
    }
    public Map<String, Integer> getMonthlyTicketSales() {
        Map<String, Integer> sales = new LinkedHashMap<>();
        String sql = "SELECT FORMAT(issuedAt, 'yyyy-MM') AS month, COUNT(*) AS count " +
                "FROM Tickets GROUP BY FORMAT(issuedAt, 'yyyy-MM') ORDER BY month";
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sales.put(rs.getString("month"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

}