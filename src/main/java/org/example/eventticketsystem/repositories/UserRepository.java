package org.example.eventticketsystem.repositories;

import org.example.eventticketsystem.dal.dao.GenericDAO;
import org.example.eventticketsystem.dal.dao.GenericDAOFactory;
import org.example.eventticketsystem.dal.dao.UserRoleDAO;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.dal.models.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRepository implements IUserRepository {
    private final GenericDAO<User> userDAO;
    private final GenericDAO<UserRole> userRoleDAO;


    public UserRepository(UserRoleDAO userRoleDAO) {
        try {
            this.userDAO = GenericDAOFactory.getDAO(User.class);
            this.userRoleDAO = GenericDAOFactory.getDAO(UserRole.class);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create UserRepository", e);
        }
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public Optional<User> findById(int id) {
        return userDAO.findById(id);
    }

    @Override
    public boolean save(User user) {
        return userDAO.save(user);
    }

    @Override
    public boolean update(User user) {
        return userDAO.update(user);
    }

    @Override
    public boolean delete(int id) {
        return userDAO.delete(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDAO.findAll().stream().filter(user -> user.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    @Override
    public List<User> findByRole(String role) {
        List<Integer> userIds = userRoleDAO.getUserIdsByRole(role);
        return userDAO.findAll().stream()
                .filter(user -> userIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Implement the method to find a user by email
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        // Implement the method to find a user by phone number
        return Optional.empty();
    }
}
