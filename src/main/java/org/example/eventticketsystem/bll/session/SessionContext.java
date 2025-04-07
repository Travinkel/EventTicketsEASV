package org.example.eventticketsystem.bll.session;

import org.example.eventticketsystem.dal.models.User;

import java.util.List;

public class SessionContext {
    private final User currentUser;
    private final List<String> roleNames;

    public SessionContext(User currentUser, List<String> roleNames) {
        this.currentUser = currentUser;
        this.roleNames = roleNames.stream()
                .map(String::toUpperCase)
                .toList();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public int getUserId() {
        return currentUser.getId();
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public boolean hasRole(String roleName) {
        return roleNames.contains(roleName.toUpperCase());
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isEventCoordinator() {
        return hasRole("EVENTCOORDINATOR");
    }

    @Override
    public String toString() {
        return currentUser.getUsername() + " (" + String.join(", ", roleNames) + ")";
    }
}
