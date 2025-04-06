package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.dal.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class UserDAO implements GenericDAO<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private final DBConnection dbConnection;

    public UserDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==== CRUD ====

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
        }
        return users;
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(extractUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO Users (username, hashedPassword, name, email, phone, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getHashedPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error saving user", e);
        }
        return false;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE Users SET username = ?, hashedPassword = ?, name = ?, email = ?, phone = ?, created_at = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getHashedPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
            ps.setInt(7, user.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("Error updating user", e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("Error deleting user", e);
        }
        return false;
    }

    // ==== Utility Methods ====

    private User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("hashedPassword"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}