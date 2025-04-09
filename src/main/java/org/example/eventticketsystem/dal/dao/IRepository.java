/**
 * 📚 Repository for managing User-Event-Role relationships.
 * This class handles CRUD operations and additional queries for the UserEventRoles table.
 * <p>
 * 🧱 Design Pattern: Data Access Object (DAO)
 * <p>
 * 🔗 Dependencies:
 * {@link org.example.eventticketsystem.dal.connection.DBConnection} for database connectivity
 * {@link org.example.eventticketsystem.dal.helpers.ResultSetExtractor} for mapping result sets to models
 * {@link org.example.eventticketsystem.dal.models.UserEventRole}, {@link Event}, {@link org.example.eventticketsystem.dal.models.User} for domain models
 */

package org.example.eventticketsystem.dal.dao;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    List<T> findAll();

    Optional<T> findById(int id);

    boolean save(T entity);

    boolean update(T entity);

    boolean delete(int id);
}