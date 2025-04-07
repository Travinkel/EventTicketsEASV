package org.example.eventticketsystem.dal.helpers;

import org.example.eventticketsystem.dal.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetExtractor {
    // Extracts a Ticket object from ResultSet
    public static Ticket extractTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setEventId(rs.getInt("eventId"));
        ticket.setUserId(rs.getInt("userId"));
        ticket.setQrCode(rs.getString("qrCode"));
        ticket.setBarcode(rs.getString("barcode"));
        ticket.setIssuedAt(rs.getTimestamp("issuedAt").toLocalDateTime());
        ticket.setCheckedIn(rs.getBoolean("checkedIn"));
        ticket.setPriceAtPurchase(rs.getDouble("priceAtPurchase"));
        return ticket;
    }

    // Extracts an Event object from ResultSet
    public static Event extractEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setStartTime(rs.getTimestamp("startTime").toLocalDateTime());
        event.setEndTime(rs.getTimestamp("endTime") != null ? rs.getTimestamp("endTime").toLocalDateTime() : null);
        event.setLocationGuidance(rs.getString("location"));
        event.setPrice(rs.getDouble("price"));
        event.setCapacity(rs.getInt("capacity"));
        event.setPublic(rs.getBoolean("isPublic"));
        return event;
    }

    // Extracts a User object from ResultSet
    public static User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setHashedPassword(rs.getString("hashedPassword"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        return user;
    }

    // Extracts a UserRole object from ResultSet
    public static UserRole extractUserRole(ResultSet rs) throws SQLException {
        UserRole userRole = new UserRole();
        userRole.setUserId(rs.getInt("userId"));
        userRole.setRoleId(rs.getInt("roleId"));
        return userRole;
    }

    // Extracts a SpecialTicket object from ResultSet
    public static SpecialTicket extractSpecialTicket(ResultSet rs) throws SQLException {
        SpecialTicket specialTicket = new SpecialTicket();
        specialTicket.setId(rs.getInt("id"));
        specialTicket.setEventId(rs.getInt("eventId"));
        specialTicket.setUserId(rs.getInt("userId"));
        specialTicket.setQrCode(rs.getString("qrCode"));
        specialTicket.setBarcode(rs.getString("barcode"));
        specialTicket.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        specialTicket.setType(rs.getString("type"));
        return specialTicket;
    }

    // Extracts a UserEventRole object from ResultSet
    public static UserEventRole extractUserEventRole(ResultSet rs) throws SQLException {
        UserEventRole userEventRole = new UserEventRole();
        userEventRole.setUserId(rs.getInt("userId"));
        userEventRole.setEventId(rs.getInt("eventId"));
        userEventRole.setRole(rs.getString("role"));
        return userEventRole;
    }

    // Extracts a Role object from ResultSet
    public static Role extractRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("id"));
        role.setName(rs.getString("name"));
        return role;
    }

}
