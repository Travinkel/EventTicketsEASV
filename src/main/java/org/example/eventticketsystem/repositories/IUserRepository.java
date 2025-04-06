package org.example.eventticketsystem.repositories;

import org.example.eventticketsystem.dal.models.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    List<User> findAll();

    Optional<User> findById(int id);

    boolean save(User user);

    boolean update(User user);

    boolean delete(int id);

    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
}
