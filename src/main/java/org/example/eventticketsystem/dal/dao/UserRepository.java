package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class UserRepository implements GenericRepository<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
    private final DBConnection dbConnection;

    public UserRepository() throws SQLException {
        this.dbConnection = DBConnection.getInstance();
        if (this.dbConnection == null || this.dbConnection.getConnection().isClosed()) {
            throw new IllegalStateException("❌ Cannot initialize UserRepository: no DB connection");
        }
    }

    public UserRepository(DBConnection dbConnection) throws SQLException {
        this.dbConnection = dbConnection;
        if (this.dbConnection == null || this.dbConnection.getConnection().isClosed()) {
            throw new IllegalStateException("❌ Cannot initialize UserRepository: no DB connection");
        }
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
                users.add(ResultSetExtractor.extractUser(rs));
            }

        } catch (SQLException e) {
            LOGGER.error("Error finding all users", e);
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
                if (rs.next()) return Optional.of(ResultSetExtractor.extractUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding user by id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO Users (username, hashedPassword, name, email, phone, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
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
            LOGGER.error("Error saving user", e);
        }
        return false;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE Users SET username = ?, hashedPassword = ?, name = ?, email = ?, phone = ?, createdAt = ? WHERE id = ?";
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
            LOGGER.error("Error updating user", e);
        }
        return false;
    }

    @Override
    public boolean delete(int userId) {
        String sql = "DELETE FROM Users WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Failed to delete user with ID: {}", userId, e);
            return false;
        }
    }

    // ==== Role Management ====

    public boolean assignRoleToUser(int userId, int roleId) {
        String sql = "INSERT INTO UserRoles (user_Id, role_Id) VALUES (?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error assigning role to user", e);
        }
        return false;
    }

    public boolean isAdmin(int userId) {
        String sql = "SELECT r.name FROM UserRoles ur JOIN Roles r ON ur.role_Id = r.id WHERE ur.user_Id = ? AND r.name = 'admin'";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking if user is admin", e);
        }
        return false;
    }

    // ==== User Retrieval ====

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding user by username", e);
        }
        return Optional.empty();
    }

    // ==== Utility Methods ====

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
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
}
