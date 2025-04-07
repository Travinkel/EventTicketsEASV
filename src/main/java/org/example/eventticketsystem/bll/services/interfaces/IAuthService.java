package org.example.eventticketsystem.bll.services.interfaces;

import org.example.eventticketsystem.dal.models.User;

public interface IAuthService {
    User authenticate(String username, String password);
    boolean hasRole(int userId, String roleName);
    String getHighestRole(int userId); // for simple GUI logic
}
