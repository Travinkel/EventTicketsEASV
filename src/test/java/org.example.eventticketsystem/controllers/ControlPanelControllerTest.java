package org.example.eventticketsystem.controllers;

import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.utils.INavigation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ControlPanelControllerTest {

    private ControlPanelController controller;
    private INavigation navigationMock;
    private UserService userService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testControllerLoadsCorrectly() {
        controller.initialize();
        // Additional asserts or verifications can go here
    }
}
