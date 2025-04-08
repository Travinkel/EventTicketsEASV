package org.example.eventticketsystem.bll.session;

import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.utils.di.Component;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Component
public class SessionContext {
    private User currentUser;
    private List<String> roleNames;

    public SessionContext() {
    }

    public void initialize(User currentUser, List<String> roleNames) {
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
