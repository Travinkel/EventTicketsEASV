package org.example.eventticketsystem.dao;

import org.example.eventticketsystem.models.User;

import java.util.List;

public interface IUserDAO {
    User findByUsername(String username);
    List<User> getAllUsers();
    boolean saveUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int id);
}
