package org.example.eventticketsystem.dal.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T> {
    List<T> findAll();
    Optional<T> findById(int id);
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(int id);
}