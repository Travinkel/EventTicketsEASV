package org.example.eventticketsystem.bll;

import org.example.eventticketsystem.dal.UserDAO;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.utils.PasswordUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Injectable
public class UserService {
    private final UserDAO userDAO;
    private final PasswordUtil passwordUtil;

    public UserService(UserDAO userDAO, PasswordUtil passwordUtil) {
        this.userDAO = userDAO;
        this.passwordUtil = passwordUtil;
        System.out.println("✅ UserService constructor called with: " + userDAO + " and " + passwordUtil);

    }

    // === Authentication ===

    public Optional<User> authenticate(String username, String plainTextPassword) {
        return userDAO.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .filter(u -> passwordUtil.verifyPassword(plainTextPassword, u.getHashedPassword()))
                .findFirst();
    }

    // === CRUD ===

    public boolean addUser(String username, String plainTextPassword, String name, String email, String role, String phone, LocalDateTime createdAt) {
        String hashed = passwordUtil.hashPassword(plainTextPassword);
        User newUser = new User(0, username, hashed, name, email, role, phone, createdAt);
        return userDAO.save(newUser);
    }

    public boolean updateUser(User user) {
        return userDAO.update(user);
    }

    public boolean removeUser(int userId) {
        return userDAO.delete(userId);
    }

    public Optional<User> getUserById(int id) {
        return userDAO.findById(id);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    // === Future-Proof Filters and Utilities ===

    public List<User> getUsersByRole(String role) {
        return userDAO.findAll().stream()
                .filter(u -> u.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    public boolean usernameExists(String username) {
        return userDAO.findAll().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    public Optional<User> getUserByUsername(String username) {
        return userDAO.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public long countByRole(String role) {
        return userDAO.findAll().stream()
                .filter(u -> u.getRole().equalsIgnoreCase(role))
                .count();
    }

    public long getTotalUserCount() {
        return userDAO.findAll().size();
    }

    public Map<String, Long> countUsersByRole() {
        return userDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        user -> user.getRole().toUpperCase(), // Normalize roles
                        Collectors.counting()
                ));
    }
    public void updatePassword(int userId, String newPassword) {
        String hashed = passwordUtil.hashPassword(newPassword);
        userDAO.updatePassword(userId, hashed);
    }

}
