package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.dal.models.Role;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Injectable
public class RoleRepository implements GenericRepository<Role> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleRepository.class);
    private final Connection connection;

    public RoleRepository() throws SQLException {
        this.connection = DBConnection.getInstance().getConnection();
        if (this.connection == null || this.connection.isClosed()) {
            throw new IllegalStateException("❌ Cannot initialize RoleRepository: no DB connection");
        }
    }


    // ==== CRUD ====

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM Roles";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(ResultSetExtractor.extractRole(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding all roles", e);
        }
        return roles;
    }

    @Override
    public Optional<Role> findById(int id) {
        String sql = "SELECT * FROM Roles WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(ResultSetExtractor.extractRole(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding role by id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Role role) {
        String sql = "INSERT INTO Roles (name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.getName());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) role.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error saving role", e);
        }
        return false;
    }

    @Override
    public boolean update(Role role) {
        String sql = "UPDATE Roles SET name = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role.getName());
            ps.setInt(2, role.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error updating role", e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Roles WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error deleting role", e);
        }
        return false;
    }
}
