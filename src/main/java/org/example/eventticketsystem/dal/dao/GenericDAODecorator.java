package org.example.eventticketsystem.dal.dao;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GenericDAODecorator<T> implements GenericDAO<T> {

    private final GenericDAO<T> decoratedDAO;

    protected GenericDAODecorator(GenericDAO<T> decoratedDAO) {
        this.decoratedDAO = decoratedDAO;
    }

    @Override
    public List<T> findAll() {
        return decoratedDAO.findAll();
    }

    @Override
    public Optional<T> findById(int id) {
        return decoratedDAO.findById(id);
    }

    @Override
    public boolean save(T entity) {
        return decoratedDAO.save(entity);
    }

    @Override
    public boolean update(T entity) {
        return decoratedDAO.update(entity);
    }

    @Override
    public boolean delete(int id) {
        return decoratedDAO.delete(id);
    }
}
