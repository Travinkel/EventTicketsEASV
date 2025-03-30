package org.example.eventticketsystem.dal;

import org.example.eventticketsystem.models.User;

import java.util.List;
import java.util.Optional;

public interface IDAO<T> {
    List<T> findAll();
    Optional<T> findById(int id);
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(int id);
}
