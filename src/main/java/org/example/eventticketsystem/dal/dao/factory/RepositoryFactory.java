package org.example.eventticketsystem.dal.dao.factory;

import org.example.eventticketsystem.dal.dao.GenericRepository;
import org.example.eventticketsystem.dal.dao.*;
import org.example.eventticketsystem.dal.models.*;
import org.example.eventticketsystem.utils.di.Injectable;

import java.sql.SQLException;

@Injectable
public class RepositoryFactory implements IRepositoryFactory {

    @Override
    public GenericRepository<User> getUserRepository() throws SQLException {
        return new UserRepository();
    }

    @Override
    public GenericRepository<Role> getRoleRepository() throws SQLException {
        return new RoleRepository();
    }

    @Override
    public UserRoleRepository getUserRoleRepository() throws SQLException {
        return new UserRoleRepository();
    }

    @Override
    public GenericRepository<Event> getEventRepository() throws SQLException {
        return new EventRepository();
    }

    @Override
    public UserEventRoleRepository getUserEventRoleRepository() throws SQLException {
        return new UserEventRoleRepository();
    }
}