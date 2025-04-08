package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.bll.services.interfaces.IAdminService;
import org.example.eventticketsystem.dal.dao.*;
import org.example.eventticketsystem.dal.models.*;
import org.example.eventticketsystem.utils.di.Inject;
import org.example.eventticketsystem.utils.di.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {

    Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final EventRepository eventRepo;
    private final UserEventRoleRepository userEventRoleRepo;

    private final Map<Integer, List<User>> coordinatorCache = new ConcurrentHashMap<>();

    @Inject
    public AdminService(
            UserRepository userRepo,
            RoleRepository roleRepo,
            UserRoleRepository userRoleRepo,
            EventRepository eventRepo,
            UserEventRoleRepository userEventRoleRepo
    ) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.eventRepo = eventRepo;
        this.userEventRoleRepo = userEventRoleRepo;
    }

    // ─────────────────── USERS ──────────────────────

    @Override
    public List<User> findAllUsers() {
        try {
            List<User> users = userRepo.findAll();
            LOGGER.info("✅ Successfully retrieved all users.");
            return users;
        } catch (Exception e) {
            LOGGER.error("❌ Failed to retrieve all users.", e);
            return List.of();
        }
    }

    @Override
    public Optional<User> findUserById(int id) {
            return userRepo.findById(id);
    }

    @Override
    public boolean createUser(User user) {
        try {
            boolean result = userRepo.save(user);
            if (result) {
                LOGGER.info("✅ Successfully created user: {}", user.getUsername());
            } else {
                LOGGER.warn("❌ Failed to create user: {}", user.getUsername());
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("❌ Error creating user: {}", user.getUsername(), e);
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        return userRepo.update(user);
    }

    @Override
    public boolean deleteUser(int userId) {
        return userRepo.delete(userId);
    }

    // ─────────────────── ROLES ──────────────────────

    @Override
    public List<Role> findAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    public List<Role> getRolesForUser(int userId) {
        return userRoleRepo.findRolesByUserId(userId);
    }

    @Override
    public boolean assignRoleToUser(int userId, int roleId) {
        return userRoleRepo.assignRoleToUser(userId, roleId);
    }

    @Override
    public boolean removeRoleFromUser(int userId, int roleId) {
        return userRoleRepo.removeRoleFromUser(userId, roleId);
    }

    @Override
    public Map<Integer, List<String>> getAllUserRolesMapped() {
        return userRoleRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        UserRole::getUserId,
                        Collectors.mapping(ur ->
                                        roleRepo.findById(ur.getRoleId())
                                                .map(role -> role.getName()) // If role exists, get name
                                                .orElse("Unknown Role"), // If no role, set default name
                                Collectors.toList())
                ));
    }

    // ─────────────────── EVENTS ──────────────────────

    @Override
    public List<Event> findAllEvents() {
        return eventRepo.findAll();
    }

    @Override
    public boolean deleteEvent(int eventId) {
        return eventRepo.delete(eventId);
    }

    @Override
    public Map<Integer, List<Event>> getAllEventsMappedByCoordinator() {
        return userEventRoleRepo.findAll().stream()
                .filter(uer -> "COORDINATOR".equals(uer.getRole()))
                .collect(Collectors.groupingBy(UserEventRole::getUserId,
                        Collectors.mapping(uer -> eventRepo.findById(uer.getEventId()).orElseThrow(), Collectors.toList())));
    }

    // ─────────────── EVENT COORDINATORS ──────────────

    @Override
    public List<Event> getEventsForCoordinator(int userId) {
        return userEventRoleRepo.findEventsByUserId(userId);
    }

    @Override
    public List<User> getCoordinatorsForEvent(int eventId) {
        return userEventRoleRepo.getCoordinatorsForEvent(eventId);
    }

    @Override
    public boolean assignCoordinatorToEvent(int userId, int eventId) {
        return userEventRoleRepo.assignRole(userId, eventId, "COORDINATOR");
    }

    @Override
    public boolean removeCoordinatorFromEvent(int userId, int eventId) {
        return userEventRoleRepo.removeRole(userId, eventId, "COORDINATOR");
    }

    public Optional<User> createUserAndReturn(User user) {
        return userRepo.saveAndReturn(user);
    }

    public Optional<Role> findRoleByName(String name) {
        return roleRepo.findAll().stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
    }

    // ─────────────────── BULK OPERATIONS ──────────────────────

    /**
     * Creates multiple users in bulk.
     * @param users List of users to create.
     * @return List of successfully created users.
     */
    public List<User> createUsersInBulk(List<User> users) {
        List<User> createdUsers = new ArrayList<>();
        for (User user : users) {
            try {
                Optional<User> createdUser = userRepo.saveAndReturn(user);
                createdUser.ifPresent(createdUsers::add);
                LOGGER.info("✅ Successfully created user: {}", user.getUsername());
            } catch (Exception e) {
                LOGGER.error("❌ Failed to create user: {}", user.getUsername(), e);
            }
        }
        return createdUsers;
    }

    /**
     * Deletes multiple users in bulk.
     * @param userIds List of user IDs to delete.
     * @return Number of successfully deleted users.
     */
    public int deleteUsersInBulk(List<Integer> userIds) {
        int deletedCount = 0;
        for (int userId : userIds) {
            if (userRepo.delete(userId)) {
                deletedCount++;
                LOGGER.info("✅ Successfully deleted user with ID: {}", userId);
            } else {
                LOGGER.warn("❌ Failed to delete user with ID: {}", userId);
            }
        }
        return deletedCount;
    }

    // ─────────────────── EVENT SEARCH ──────────────────────

    /**
     * Searches for events by title.
     * @param title Partial or full title of the event.
     * @return List of matching events.
     */
    public List<Event> searchEventsByTitle(String title) {
        return eventRepo.findAll().stream()
                .filter(event -> event.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    /**
     * Searches for events within a date range.
     * @param startDate Start date of the range.
     * @param endDate End date of the range.
     * @return List of matching events.
     */
    public List<Event> searchEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepo.findAll().stream()
                .filter(event -> !event.getStartTime().isBefore(startDate) && !event.getEndTime().isAfter(endDate))
                .toList();
    }

    // ─────────────────── ROLE MANAGEMENT ──────────────────────

    /**
     * Updates the name of a role.
     * @param roleId ID of the role to update.
     * @param newName New name for the role.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateRoleName(int roleId, String newName) {
        Optional<Role> roleOpt = roleRepo.findById(roleId);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            role.setName(newName);
            return roleRepo.update(role);
        }
        LOGGER.warn("❌ Role with ID {} not found.", roleId);
        return false;
    }

    /**
     * Deletes a role by ID.
     * @param roleId ID of the role to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean deleteRole(int roleId) {
        return roleRepo.delete(roleId);
    }

    /**
     * Assigns multiple roles to a user.
     * @param userId ID of the user.
     * @param roleIds List of role IDs to assign.
     * @return Number of successfully assigned roles.
     */
    public int assignRolesToUser(int userId, List<Integer> roleIds) {
        int assignedCount = 0;
        for (int roleId : roleIds) {
            if (userRoleRepo.assignRoleToUser(userId, roleId)) {
                assignedCount++;
            }
        }
        return assignedCount;
    }

    public boolean createUserWithRole(User user, String roleName) {
        Optional<User> saved = userRepo.saveAndReturn(user);
        if (saved.isPresent()) {
            Optional<Role> role = roleRepo.findByName(roleName);
            role.ifPresent(r -> userRoleRepo.assignRoleToUser(saved.get().getId(), r.getId()));
            return true;
        }
        return false;
    }

    public List<User> findAllUsersWithRole(String roleName) {
        // Find the role by name
        Optional<Role> roleOpt = roleRepo.findAll().stream()
                .filter(role -> role.getName().equalsIgnoreCase(roleName))
                .findFirst();

        // If the role doesn't exist, return an empty list
        if (roleOpt.isEmpty()) {
            return List.of();
        }

        // Get the role ID
        int roleId = roleOpt.get().getId();

        // Find all user-role mappings for the role ID
        List<UserRole> userRoles = userRoleRepo.findAll().stream()
                .filter(userRole -> userRole.getRoleId() == roleId)
                .toList();

        // Retrieve users based on the user IDs in the mappings
        return userRoles.stream()
                .map(userRole -> userRepo.findById(userRole.getUserId()).orElse(null))
                .filter(user -> user != null) // Filter out null users
                .toList();
    }

    /**
     * Removes all roles from a user.
     * @param userId ID of the user.
     * @return True if all roles were successfully removed, false otherwise.
     */
    public boolean removeAllRolesFromUser(int userId) {
        List<Role> roles = userRoleRepo.findRolesByUserId(userId);
        boolean success = true;
        for (Role role : roles) {
            if (!userRoleRepo.removeRoleFromUser(userId, role.getId())) {
                success = false;
            }
        }
        return success;
    }
}
