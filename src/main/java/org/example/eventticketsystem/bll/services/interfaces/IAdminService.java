package org.example.eventticketsystem.bll.services.interfaces;

import org.example.eventticketsystem.dal.models.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IAdminService {

    // ─── User Management ─────────────────────────────────────────────

    List<User> findAllUsers();
    Optional<User> findUserById(int id);
    boolean createUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int userId);

    // ─── Role Management ─────────────────────────────────────────────

    List<Role> findAllRoles(); // For populating dropdowns, etc.
    List<Role> getRolesForUser(int userId);
    boolean assignRoleToUser(int userId, int roleId);
    boolean removeRoleFromUser(int userId, int roleId);
    Map<Integer, List<String>> getAllUserRolesMapped();

    // ─── Event Deletion ──────────────────────────────────────────────

    List<Event> findAllEvents(); // So the admin can delete events
    boolean deleteEvent(int eventId);

    // ─── Coordinator Assignment ──────────────────────────────────────

    List<Event> getEventsForCoordinator(int userId); // For the user composite
    boolean assignCoordinatorToEvent(int userId, int eventId);
    boolean removeCoordinatorFromEvent(int userId, int eventId);
    List<User> getCoordinatorsForEvent(int eventId);
    Map<Integer, List<Event>> getAllEventsMappedByCoordinator();
}
