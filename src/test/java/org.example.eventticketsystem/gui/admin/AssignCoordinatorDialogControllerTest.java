package org.example.eventticketsystem.gui.admin;

import javafx.scene.control.ComboBox;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.gui.coordinator.AssignCoordinatorDialogController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class AssignCoordinatorDialogControllerTest {

    private AssignCoordinatorDialogController controller;
    private AdminService adminServiceMock;

    @BeforeEach
    void setUp() {
        adminServiceMock = mock(AdminService.class);
        controller = new AssignCoordinatorDialogController(adminServiceMock);

        // Mock JavaFX components
        controller.coordinatorComboBox = new ComboBox<>();
        controller.eventComboBox = new ComboBox<>();
    }

    @Test
    void testInitialize() {
        List<User> mockCoordinators = List.of(new User(1, "coord1", "Coordinator 1", "coord1@example.com"));
        List<Event> mockEvents = List.of(new Event(1, "Event 1"));

        when(adminServiceMock.findAllUsersWithRole("COORDINATOR")).thenReturn(mockCoordinators);
        when(adminServiceMock.findAllEvents()).thenReturn(mockEvents);

        controller.initialize();

        Assertions.assertEquals(1, controller.coordinatorComboBox.getItems()
                .size());
        Assertions.assertEquals(1, controller.eventComboBox.getItems()
                .size());
    }

    @Test
    void testHandleAssign() {
        User mockUser = new User(1, "coord1", "Coordinator 1", "coord1@example.com");
        Event mockEvent = new Event(1, "Event 1");

        controller.coordinatorComboBox.getItems()
                .add(mockUser);
        controller.eventComboBox.getItems()
                .add(mockEvent);
        controller.coordinatorComboBox.setValue(mockUser);
        controller.eventComboBox.setValue(mockEvent);

        when(adminServiceMock.assignCoordinatorToEvent(mockUser.getId(), mockEvent.getId())).thenReturn(true);

        controller.handleAssign();

        Assertions.assertTrue(controller.isCoordinatorAssigned());
        verify(adminServiceMock, times(1)).assignCoordinatorToEvent(mockUser.getId(), mockEvent.getId());
    }
}
