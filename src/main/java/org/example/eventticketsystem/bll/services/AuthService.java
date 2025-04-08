package org.example.eventticketsystem.bll.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.dal.dao.UserRepository;
import org.example.eventticketsystem.dal.dao.UserRoleRepository;
import org.example.eventticketsystem.dal.models.Role;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.di.Service;
import org.example.eventticketsystem.utils.di.Injector;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final SessionContext sessionContext;

    /**
     * Constructor for AuthService.
     *
     * @param userRepository The UserRepository instance for database operations.
     * @param userRoleRepository The UserRoleRepository instance for role-related operations.
     */
    public AuthService(UserRepository userRepository, UserRoleRepository userRoleRepository, SessionContext sessionContext) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.sessionContext = sessionContext;
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
                        .toList();

                sessionContext.initialize(user, roleNames);
                return Optional.of(sessionContext);
            }
        }
        return Optional.empty();
    }
}
