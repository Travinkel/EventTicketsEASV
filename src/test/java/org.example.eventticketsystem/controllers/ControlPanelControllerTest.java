package org.example.eventticketsystem.controllers;

import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.models.UserRole;
import org.example.eventticketsystem.services.UserService;
import org.example.eventticketsystem.utils.INavigation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ControlPanelControllerTest {

    private ControlPanelController controller;
    private INavigation navigationMock;
    private UserService userService;

    @BeforeEach
    void setUp() {
        navigationMock = mock(INavigation.class);
        userService = new UserService();
        User admin = new User("admin", "Admin User", "admin@example.com", "pass", UserRole.ADMIN);
        controller = new ControlPanelController(navigationMock, userService);
        controller.setUser(admin); // Optional if controller supports it
    }

    @Test
    void testControllerLoadsCorrectly() {
        controller.initialize();
        // Additional asserts or verifications can go here
    }
}
