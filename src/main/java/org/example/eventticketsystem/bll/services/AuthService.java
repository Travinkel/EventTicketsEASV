package org.example.eventticketsystem.bll.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.dal.dao.UserRepository;
import org.example.eventticketsystem.dal.dao.UserRoleRepository;
import org.example.eventticketsystem.dal.models.Role;
import org.example.eventticketsystem.dal.models.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * Constructor for AuthService.
     *
     * @param userRepository The UserRepository instance for database operations.
     * @param userRoleRepository The UserRoleRepository instance for role-related operations.
     */
    public AuthService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * Attempts to log in a user with the given username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return An Optional containing the SessionContext if login is successful, or an empty Optional if not.
     */
    public Optional<SessionContext> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean valid = BCrypt.verifyer()
                    .verify(password.toCharArray(), user.getHashedPassword().toCharArray()).verified;
            if (valid) {
                List<String> roleNames = userRoleRepository
                        .findRolesByUserId(user.getId())
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());
                return Optional.of(new SessionContext(user, roleNames));
            }
        }
        return Optional.empty();
    }
}
