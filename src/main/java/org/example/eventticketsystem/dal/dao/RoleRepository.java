/**
 * 📚 Repository for managing Role entities and their relationships with users and events.
 * This class provides CRUD operations and additional queries for the `Roles` table.
 * It also supports operations related to the `UserEventRoles` table, enabling role-based
 * access control and user-event-role associations.
 * <p>
 * 🧱 Design Pattern: Data Access Object (DAO)
 * - Encapsulates database access logic for the `Roles` table.
 * - Promotes separation of concerns by isolating persistence logic from business logic.
 * <p>
 * 🔗 Dependencies:
 * - {@link org.example.eventticketsystem.dal.connection.DBConnection} for establishing database connections.
 * - {@link org.example.eventticketsystem.dal.helpers.ResultSetExtractor} for mapping SQL result sets to domain models.
 * - {@link org.example.eventticketsystem.dal.models.Role} for representing role entities.
 * - {@link org.example.eventticketsystem.dal.models.UserEventRole} for managing user-event-role relationships.
 * - {@link org.example.eventticketsystem.dal.models.User} and {@link Event} for domain models related to roles.
 * <p>
 * 🧩 Responsibilities:
 * - Perform CRUD operations on the `Roles` table.
 * - Support role-based queries, such as finding roles by name or ID.
 * - Facilitate role assignments and associations with users and events.
 * - Ensure transactional integrity and handle SQL exceptions gracefully.
 * <p>
 * 🛠️ Usage:
 * - This repository is managed as a singleton by the DI framework.
 * - Use this class in service layers to interact with role-related data.
 */
package org.example.eventticketsystem.dal.dao;

import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.dal.helpers.ResultSetExtractor;
import org.example.eventticketsystem.dal.models.Role;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing CRUD operations on Role entities.
 * This class is a singleton and managed by the DI framework.
 */
@Injectable
@Singleton
@Scope("singleton")
public class RoleRepository implements IRepository<Role> {

    private static final Logger
            LOGGER =
            LoggerFactory.getLogger(RoleRepository.class);
    private final DBConnection
            dbConnection;

    /**
     * Constructor for RoleRepository.
     * The DI framework injects the DBConnection dependency.
     *
     * @param dbConnection The database connection instance.
     */
    @Inject
    public RoleRepository(DBConnection dbConnection) {
        this.dbConnection =
                dbConnection;
    }

    // ==== CRUD ====

    /**
     * Retrieves all roles from the database.
     *
     * @return A list of all roles.
     */
    @Override
    public List<Role> findAll() {
        LOGGER.info("📦 Retrieving all roles from the database...");
        List<Role>
                roles =
                new ArrayList<>();
        String
                sql =
                "SELECT * FROM Roles";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(ResultSetExtractor.extractRole(rs));
            }
            LOGGER.info("✅ Successfully retrieved {} roles.",
                    roles.size());
        } catch (SQLException e) {
            LOGGER.error("❌ Error retrieving all roles from the database.",
                    e);
        }
        return roles;
    }

    /**
     * Finds a role by its ID.
     *
     * @param id The ID of the role.
     * @return An Optional containing the role if found, or empty if not.
     */
    @Override
    public Optional<Role> findById(int id) {
        LOGGER.debug("🔍 Finding role by ID: {}",
                id);
        String
                sql =
                "SELECT * FROM Roles WHERE id = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1,
                    id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("✅ Role with ID {} found.",
                            id);
                    return Optional.of(ResultSetExtractor.extractRole(rs));
                }
            }
            LOGGER.warn("⚠️ No role found with ID: {}",
                    id);
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding role by ID: {}",
                    id,
                    e);
        }
        return Optional.empty();
    }

    /**
     * Saves a new role to the database.
     *
     * @param role The role to save.
     * @return True if the role was saved successfully, false otherwise.
     */
    @Override
    public boolean save(Role role) {
        LOGGER.info("➕ Saving new role: {}",
                role.getName());
        String
                sql =
                "INSERT INTO Roles (name) VALUES (?)";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,
                    role.getName());
            int
                    rows =
                    ps.executeUpdate();
            if (rows ==
                0) {
                LOGGER.warn("⚠️ No rows affected while saving role: {}",
                        role.getName());
                return false;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    role.setId(keys.getInt(1));
                    LOGGER.info("✅ Role '{}' saved with ID: {}",
                            role.getName(),
                            role.getId());
                }
            }
            return true;
        } catch (SQLException e) {
            LOGGER.error("❌ Error saving role: {}",
                    role.getName(),
                    e);
        }
        return false;
    }

    /**
     * Updates an existing role in the database.
     *
     * @param role The role to update.
     * @return True if the role was updated successfully, false otherwise.
     */
    @Override
    public boolean update(Role role) {
        LOGGER.info("♻️ Updating role with ID {}: {}",
                role.getId(),
                role.getName());
        String
                sql =
                "UPDATE Roles SET name = ? WHERE id = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setString(1,
                    role.getName());
            ps.setInt(2,
                    role.getId());
            if (ps.executeUpdate() ==
                1) {
                LOGGER.info("✅ Role with ID {} successfully updated.",
                        role.getId());
                return true;
            }
            LOGGER.warn("⚠️ No rows affected while updating role with ID: {}",
                    role.getId());
        } catch (SQLException e) {
            LOGGER.error("❌ Error updating role with ID {}: {}",
                    role.getId(),
                    e);
        }
        return false;
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id The ID of the role to delete.
     * @return True if the role was deleted successfully, false otherwise.
     */
    @Override
    public boolean delete(int id) {
        LOGGER.info("🗑 Deleting role with ID: {}",
                id);
        String
                sql =
                "DELETE FROM Roles WHERE id = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1,
                    id);
            if (ps.executeUpdate() ==
                1) {
                LOGGER.info("✅ Role with ID {} successfully deleted.",
                        id);
                return true;
            }
            LOGGER.warn("⚠️ No rows affected while deleting role with ID: {}",
                    id);
        } catch (SQLException e) {
            LOGGER.error("❌ Error deleting role with ID: {}",
                    id,
                    e);
        }
        return false;
    }

    /**
     * Finds a role by its name.
     *
     * @param name The name of the role.
     * @return An Optional containing the role if found, or empty if not.
     */
    public Optional<Role> findByName(String name) {
        LOGGER.debug("🔍 Finding role by name: {}",
                name);
        String
                sql =
                "SELECT * FROM Roles WHERE name = ?";
        try (PreparedStatement ps = dbConnection.getConnection()
                .prepareStatement(sql)) {
            ps.setString(1,
                    name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("✅ Role with name '{}' found.",
                            name);
                    return Optional.of(ResultSetExtractor.extractRole(rs));
                }
            }
            LOGGER.warn("⚠️ No role found with name: {}",
                    name);
        } catch (SQLException e) {
            LOGGER.error("❌ Error finding role by name: {}",
                    name,
                    e);
        }
        return Optional.empty();
    }
}
