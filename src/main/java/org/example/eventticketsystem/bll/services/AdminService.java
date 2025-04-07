package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.bll.services.interfaces.IAdminService;
import org.example.eventticketsystem.dal.dao.*;
import org.example.eventticketsystem.dal.models.*;
import org.example.eventticketsystem.dal.dao.factory.RepositoryFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminService implements IAdminService {

    private final GenericRepository<User> userRepo;
    private final GenericRepository<Role> roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final GenericRepository<Event> eventRepo;
    private final UserEventRoleRepository userEventRoleRepo;


    public AdminService(RepositoryFactory repositoryFactory) throws SQLException {
        this.userRepo = repositoryFactory.getUserRepository();
        this.roleRepo = repositoryFactory.getRoleRepository();
        this.userRoleRepo = repositoryFactory.getUserRoleRepository();
        this.eventRepo = repositoryFactory.getEventRepository();
        this.userEventRoleRepo = repositoryFactory.getUserEventRoleRepository();
    }

    // ─────────────────── USERS ──────────────────────

    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Optional<User> findUserById(int id) {
        return userRepo.findById(id);
    }

    @Override
    public boolean createUser(User user) {
        return userRepo.save(user);
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
}
