package org.example.eventticketsystem.dal.dao.decorator;

import lombok.extern.slf4j.Slf4j;
import org.example.eventticketsystem.dal.dao.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Slf4j
public class LoggingRepositoryDecorator<T> extends IRepositoryDecorator<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingRepositoryDecorator.class);

    public LoggingRepositoryDecorator(IRepository<T> decoratedDAO) {
        super(decoratedDAO);
    }

    @Override
    public List<T> findAll() {
        log.info("Finding all entities of type: {}", getEntityClass().getSimpleName());
        return super.findAll();
    }

    @Override
    public Optional<T> findById(int id) {
        log.info("Finding entity of type {} with ID: {}", getEntityClass().getSimpleName(), id);
        return super.findById(id);
    }

    @Override
    public boolean save(T entity) {
        log.info("Saving entity of type {}: {}", getEntityClass().getSimpleName(), entity);
        return super.save(entity);
    }

    @Override
    public boolean update(T entity) {
        log.info("Updating entity of type {}: {}", getEntityClass().getSimpleName(), entity);
        return super.update(entity);
    }

    @Override
    public boolean delete(int id) {
        log.info("Deleting entity of type {} with ID: {}", getEntityClass().getSimpleName(), id);
        return super.delete(id);
    }

    private Class<Object> getEntityClass() {
        // Assuming T is a generic type, we can use reflection to get the class
        // In a real-world scenario, you might want to pass the class type as a parameter
        return (Class<Object>) ((java.lang.reflect.ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
