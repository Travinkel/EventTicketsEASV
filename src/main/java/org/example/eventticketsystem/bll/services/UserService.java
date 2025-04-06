// UserService.java
package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.dal.dao.UserRoleDAO;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.repositories.IUserRepository;
import org.example.eventticketsystem.repositories.UserRepository;
import org.example.eventticketsystem.utils.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final IUserRepository userRepository;
    private final PasswordUtil passwordUtil;

    public UserService(UserRoleDAO userRoleDAO, PasswordUtil passwordUtil) throws SQLException {
        this.userRepository = new UserRepository(userRoleDAO);
        this.passwordUtil = passwordUtil;
    }

    // === Authentication ===

    public Optional<User> authenticate(String username, String plainTextPassword) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordUtil.verifyPassword(plainTextPassword, u.getHashedPassword()));
    }

    // === CRUD ===

    public boolean addUser(String username, String plainTextPassword, String name, String email, String role, String phone, LocalDateTime createdAt) {
        String hashed = passwordUtil.hashPassword(plainTextPassword);
        User newUser = new User(0, username, hashed, name, email, phone, createdAt);
        boolean saved = userRepository.save(newUser);
        if (!saved) return false;

        // Get inserted user ID and assign role
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> userRoleDAO.addRole(u.getId(), role)).orElse(false);
    }

    public boolean updateUser(User user) {
        return userRepository.update(user);
    }

    public boolean removeUser(int userId) {
        return userRepository.delete(userId);
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // === Role-related Methods (via UserRoleDAO) ===

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public long countByRole(String role) {
        return userRepository.findByRole(role).size();
    }

    // === Utility ===

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public long getTotalUserCount() {
        return userRepository.findAll().size();
    }
}