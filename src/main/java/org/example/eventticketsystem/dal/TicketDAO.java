package org.example.eventticketsystem.dal;

import org.example.eventticketsystem.models.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDAO implements IDAO<Ticket> {

    private final Connection connection;

    public TicketDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(extractTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    @Override
    public Optional<Ticket> findById(int id) {
        String sql = "SELECT * FROM Tickets WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(extractTicket(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Ticket ticket) {
        String sql = "INSERT INTO Tickets (eventId, userId, qrCode, issuedAt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getEventId());
            ps.setInt(2, ticket.getUserId());
            ps.setString(3, ticket.getQrCode());
            ps.setTimestamp(4, Timestamp.valueOf(ticket.getIssuedAt()));
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ticket.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Ticket ticket) {
        String sql = "UPDATE Tickets SET eventId = ?, userId = ?, qrCode = ?, issuedAt = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ticket.getEventId());
            ps.setInt(2, ticket.getUserId());
            ps.setString(3, ticket.getQrCode());
            ps.setTimestamp(4, Timestamp.valueOf(ticket.getIssuedAt()));
            ps.setInt(5, ticket.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Tickets WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- Additional DAO Methods ---

    public int getTotalTicketCount() {
        String sql = "SELECT COUNT(*) AS total FROM Tickets";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTicketCountByEvent(int eventId) {
        String sql = "SELECT COUNT(*) AS total FROM Tickets WHERE eventId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Ticket> findByUserId(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE userId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tickets.add(extractTicket(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> findByEventId(int eventId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE eventId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tickets.add(extractTicket(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public boolean deleteByEventId(int eventId) {
        String sql = "DELETE FROM Tickets WHERE eventId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Ticket extractTicket(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getInt("id"),
                rs.getInt("eventId"),
                rs.getInt("userId"),
                rs.getString("qrCode"),
                rs.getTimestamp("issuedAt").toLocalDateTime()
        );
    }
}