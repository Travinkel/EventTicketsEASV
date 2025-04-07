package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.dal.models.Role;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.dal.models.UserRole;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class UserRoleRepository implements GenericRepository<UserRole> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleRepository.class);
    private final DBConnection dbConnection;

    public UserRoleRepository() throws SQLException {
        this.dbConnection = DBConnection.getInstance();
        if (this.dbConnection == null || this.dbConnection.getConnection().isClosed()) {
            throw new IllegalStateException("❌ Cannot initialize UserRepository: no DB connection");
        }
    }

    public UserRoleRepository(DBConnection dbConnection) throws SQLException {
        this.dbConnection = dbConnection;
        if (this.dbConnection == null || this.dbConnection.getConnection().isClosed()) {
            throw new IllegalStateException("❌ Cannot initialize UserRepository: no DB connection");
        }
    }

    // ─────────────────────── CRUD ─────────────────────────

    @Override
    public List<UserRole> findAll() {
        List<UserRole> roles = new ArrayList<>();
        String sql = "SELECT * FROM UserRoles";
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(ResultSetExtractor.extractUserRole(rs));
            }
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
        String sql = "INSERT INTO UserRoles (user_id, role_id) VALUES (?, ?)";
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Error assigning role", e);
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

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                roles.add(new Role(id, name));
            }

        } catch (SQLException e) {
            LOGGER.error("❌ Error finding roles by user ID", e);
        }

        return roles;
    }

    public List<Integer> findUserIdsByRole(int roleId) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM UserRoles WHERE role_id = ?";
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding user IDs by role ID", e);
        }
        return userIds;
    }


    public boolean removeRoleFromUser(int userId, int roleId) {
        String sql = "DELETE FROM UserRoles WHERE user_id = ? AND role_id = ?";
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("❌ Error removing role", e);
        }
        return false;
    }
}
