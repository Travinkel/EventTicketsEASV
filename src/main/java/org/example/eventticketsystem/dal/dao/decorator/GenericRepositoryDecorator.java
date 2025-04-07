package org.example.eventticketsystem.dal.dao.decorator;

import lombok.RequiredArgsConstructor;
import org.example.eventticketsystem.dal.dao.GenericRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GenericRepositoryDecorator<T> implements GenericRepository<T> {

    private final GenericRepository<T> decoratedDAO;

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
