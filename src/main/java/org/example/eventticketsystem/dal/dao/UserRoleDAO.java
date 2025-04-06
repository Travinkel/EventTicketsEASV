package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.dal.models.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Injectable
public class UserRoleDAO implements GenericDAO<UserRole> {


    private final Connection connection;

    public UserRoleDAO() throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            throw new IllegalStateException("❌ Cannot initialize UserDAO: no DB connection");
        }
        this.connection = conn;
    }

    public List<UserRole> findAll() {
        List<UserRole> roles = new ArrayList<>();
        String sql = "SELECT * FROM UserRoles";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(new UserRole(rs.getInt("user_Id"), rs.getString("role_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public List<String> getRolesForUser(int userId) {
        List<String> roles = new ArrayList<>();

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT r.name FROM UserRoles ur " +
                             "JOIN Roles r ON ur.roleId = r.id WHERE ur.userId = ?")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                roles.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roles;
    }


    public List<Integer> getUserIdsByRole(String role) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_Id FROM UserRoles WHERE role_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("user_Id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }

    public List<String> getRolesByUserId(int userId) {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT role_name FROM UserRoles WHERE user_Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("role_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public boolean addRole(int userId, String role) {
        String sql = "INSERT INTO UserRoles (user_Id, role_name) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, role.toUpperCase());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeRole(int userId, String role) {
        String sql = "DELETE FROM UserRoles WHERE user_Id = ? AND role_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, role);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
