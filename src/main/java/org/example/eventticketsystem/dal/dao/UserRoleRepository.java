package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.example.eventticketsystem.dal.models.Role;
import org.example.eventticketsystem.dal.models.UserRole;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Repository;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing User-Role relationships.
 * This class is managed by the DI framework.
 */
@Repository
@Singleton
@Scope("singleton")
public class UserRoleRepository implements IRepository<UserRole> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleRepository.class);
    private final DBConnection dbConnection;

    /**
     * Constructor for UserRoleRepository.
     * The DI framework injects the DBConnection dependency.
     *
     * @param dbConnection The database connection instance.
     */
    @Inject
    public UserRoleRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ─────────────────────── CRUD ─────────────────────────

    @Override
    public List<UserRole> findAll() {
        List<UserRole> roles = new ArrayList<>();
        String sql = "SELECT * FROM UserRoles";

        LOGGER.debug("📦 Executing SQL query to fetch all user roles: {}", sql);

        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(ResultSetExtractor.extractUserRole(rs));
            }

            LOGGER.info("✅ Successfully fetched {} user roles from the database.", roles.size());

        } catch (SQLException e) {
            LOGGER.error("❌ Error fetching all UserRoles", e);
        }

        return roles;
    }

    @Override
    public Optional<UserRole> findById(int id) {
        // This method is not applicable for UserRole as it has a composite key
        throw new UnsupportedOperationException("findById is not supported for UserRole");
    }

    @Override
    public boolean save(UserRole userRole) {
        return assignRoleToUser(userRole.getUserId(), userRole.getRoleId());
    }


    @Override
    public boolean update(UserRole userRole) {
        // This method is not applicable for UserRole as it has a composite key
        throw new UnsupportedOperationException("update is not supported for UserRole");
    }

    @Override
    public boolean delete(int id) {
        // This method is not applicable for UserRole as it has a composite key
        throw new UnsupportedOperationException("delete is not supported for UserRole");
    }

    // ─────────────── ADMIN SERVICE HELPERS ────────────────

    public boolean assignRoleToUser(int userId, int roleId) {
        String sql = "INSERT INTO UserRoles (userId, roleId) VALUES (?, ?)";

        LOGGER.debug("➕ Assigning role ID {} to user ID {}", roleId, userId);

        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);

            boolean success = ps.executeUpdate() == 1;
            if (success) {
                LOGGER.info("➕ Role ID {} successfully assigned to user ID {}", roleId, userId);
            } else {
                LOGGER.warn("⚠️ Failed to assign role ID {} to user ID {}", roleId, userId);
            }

            return success;

        } catch (SQLException e) {
            LOGGER.error("❌ Error assigning role ID {} to user ID {}", roleId, userId, e);
        }

        return false;
    }

    public List<Role> findRolesByUserId(int userId) {
        List<Role> roles = new ArrayList<>();
        String sql = """
                SELECT r.id, r.name
                FROM Roles r
                INNER JOIN UserRoles ur ON r.id = ur.roleId
                WHERE ur.userId = ?
                """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                roles.add(new Role(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            LOGGER.error("❌ Error finding roles by user ID", e);
        }

        return roles;
    }

    public List<Integer> findUserIdsByRole(int roleId) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT userId FROM UserRoles WHERE roleId = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("userId"));
            }
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding user IDs by role ID", e);
        }
        return userIds;
    }


    public boolean removeRoleFromUser(int userId, int roleId) {
        String sql = "DELETE FROM UserRoles WHERE userId = ? AND roleId = ?";

        LOGGER.debug("🗑 Removing role ID {} from user ID {}", roleId, userId);

        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);

            boolean success = ps.executeUpdate() == 1;
            if (success) {
                LOGGER.info("🗑 Role ID {} successfully removed from user ID {}", roleId, userId);
            } else {
                LOGGER.warn("⚠️ No role ID {} found for user ID {}", roleId, userId);
            }

            return success;

        } catch (SQLException e) {
            LOGGER.error("❌ Error removing role ID {} from user ID {}", roleId, userId, e);
        }

        return false;
    }
}

