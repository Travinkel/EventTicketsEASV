package org.example.eventticketsystem.dal.dao.factory;

import org.example.eventticketsystem.dal.dao.GenericRepository;
import org.example.eventticketsystem.dal.dao.UserRoleRepository;
import org.example.eventticketsystem.dal.dao.UserEventRoleRepository;
import org.example.eventticketsystem.dal.models.*;

import java.sql.SQLException;

public interface IRepositoryFactory {
    GenericRepository<User> getUserRepository() throws SQLException;
    GenericRepository<Role> getRoleRepository() throws SQLException;
    UserRoleRepository getUserRoleRepository() throws SQLException;
    GenericRepository<Event> getEventRepository() throws SQLException;
    UserEventRoleRepository getUserEventRoleRepository() throws SQLException;
}