package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Repository;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing CRUD operations on User entities.
 * This class is managed by the DI framework.
 */
@Repository
@Singleton
@Scope("singleton")
public class UserRepository implements IRepository<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
    private final DBConnection dbConnection;

    /**
     * Constructor for UserRepository.
     * The DI framework injects the DBConnection dependency.
     *
     * @param dbConnection The database connection instance.
     */
    @Inject
    public UserRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==== CRUD ====

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";

        LOGGER.debug("📦 Executing SQL query to fetch all users: {}", sql);

        try (Connection connection = dbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(ResultSetExtractor.extractUser(rs));
            }

            LOGGER.info("✅ Successfully fetched {} users from the database.", users.size());

        } catch (SQLException e) {
            LOGGER.error("❌ Error finding all users", e);
        }

        return users;
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?";

        LOGGER.debug("📦 Executing SQL query to find user by ID: {}", id);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("✅ User with ID {} found.", id);
                    return Optional.of(ResultSetExtractor.extractUser(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.error("❌ Error finding user by ID: {}", id, e);
        }

        LOGGER.warn("⚠️ No user found with ID: {}", id);
        return Optional.empty();
    }

    @Override
    public boolean save(User user) {
        String sql =
                "INSERT INTO Users (username, hashedPassword, name, email, phone, createdAt) VALUES (?, ?, ?, ?, ?, ?)";

        LOGGER.debug("📦 Preparing to save user: {}", user);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getHashedPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));

            int rows = ps.executeUpdate();
            if (rows == 0) {
                LOGGER.warn("⚠️ No rows affected while saving user: {}", user);
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                    LOGGER.info("➕ User created with ID: {}", user.getId());
                }
            }

            return true;

        } catch (SQLException e) {
            LOGGER.error("❌ Error saving user: {}", user, e);
        }

        return false;
    }

    @Override
    public boolean update(User user) {
        String sql =
                "UPDATE Users SET username = ?, hashedPassword = ?, name = ?, email = ?, phone = ?, createdAt = ? WHERE id = ?";

        LOGGER.debug("♻️ Preparing to update user: {}", user);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getHashedPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
            ps.setInt(7, user.getId());

            boolean success = ps.executeUpdate() == 1;
            if (success) {
                LOGGER.info("♻️ User with ID {} successfully updated.", user.getId());
            } else {
                LOGGER.warn("⚠️ No rows affected while updating user with ID: {}", user.getId());
            }

            return success;

        } catch (SQLException e) {
            LOGGER.error("❌ Error updating user: {}", user, e);
        }

        return false;
    }

    @Override
    public boolean delete(int userId) {
        String sql = "DELETE FROM Users WHERE id = ?";

        LOGGER.debug("🗑 Preparing to delete user with ID: {}", userId);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            boolean success = stmt.executeUpdate() > 0;

            if (success) {
                LOGGER.info("🗑 User with ID {} successfully deleted.", userId);
            } else {
                LOGGER.warn("⚠️ No user found to delete with ID: {}", userId);
            }

            return success;

        } catch (SQLException e) {
            LOGGER.error("❌ Failed to delete user with ID: {}", userId, e);
            return false;
        }
    }


    // ==== Role Management ====

    public boolean assignRoleToUser(int userId, int roleId) {
        String sql = "INSERT INTO UserRoles (userId, roleId) VALUES (?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            LOGGER.error("❌ Error assigning role to user", e);
        }

        return false;
    }

    public boolean isAdmin(int userId) {
        String sql =
                "SELECT r.name FROM UserRoles ur JOIN Roles r ON ur.roleId = r.id WHERE ur.userId = ? AND r.name = 'admin'";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // 'admin' found
            }

        } catch (SQLException e) {
            LOGGER.error("❌ Error checking if user is admin", e);
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
                if (rs.next()) return Optional.of(ResultSetExtractor.extractUser(rs));
            }

        } catch (SQLException e) {
            LOGGER.error("❌ Error finding user by username: {}", username, e);
        }

        return Optional.empty();
    }

    public Optional<User> saveAndReturn(User user) {
        String sql =
                "INSERT INTO Users (username, hashedPassword, name, email, phone, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getHashedPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setId(id);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // replace with logger
        }
        return Optional.empty();
    }

}
