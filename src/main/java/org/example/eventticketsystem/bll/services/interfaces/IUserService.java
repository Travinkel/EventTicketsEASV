package org.example.eventticketsystem.bll.services.interfaces;

import org.example.eventticketsystem.dal.models.User;

import java.util.List;

public interface IUserService {

    // CRUD operations
    List<User> getAllUsers();
    User getUserById(int userId);
    boolean createUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int userId);

    // Filtering
    List<User> getAllAdmins();
    List<User> getAllEventCoordinators();

    // Validation
    boolean emailExists(String email);
}
