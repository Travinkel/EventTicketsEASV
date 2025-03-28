/*
package org.example.eventticketsystem.dao;

import org.example.eventticketsystem.models.Event;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.models.UserRole;
import org.example.eventticketsystem.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    public List<Event> getAllEvents(){
        List<Event> events = new ArrayList<>();
        String sql = "SELCT * FROM Events";
        try (Connection connection = DBConnection.getInstance().getConnection();
            Statement stmt = connection.createStatement();
        ) {
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }
}*/
