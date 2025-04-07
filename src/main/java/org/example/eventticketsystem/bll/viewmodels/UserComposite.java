package org.example.eventticketsystem.bll.viewmodels;

import lombok.Getter;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import java.util.stream.Collectors;
import java.util.List;

public class UserComposite {
    @Getter
    private final User user;
    private final List<String> roles;
    private final List<Event> events;

    public UserComposite(User user, List<String> roles, List<Event> events) {
        this.user = user;
        this.roles = roles;
        this.events = events;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getName() {
        return user.getName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getJoinedRoles() {
        return String.join(", ", roles);
    }

    public String getJoinedEvents() {
        return events.stream().map(Event::getTitle).collect(Collectors.joining(", "));
    }
}