package org.example.eventticketsystem.gui.admin;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.eventticketsystem.bll.services.AdminService;
import org.example.eventticketsystem.bll.services.interfaces.INavigationService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class AdminControllerTest {

    private AdminController adminController;
    private AdminService adminServiceMock;
    private INavigationService navigationServiceMock;
    private SessionContext sessionMock;

    @BeforeEach
    void setUp() {
        adminServiceMock = mock(AdminService.class);
        navigationServiceMock = mock(INavigationService.class);
        sessionMock = mock(SessionContext.class);

        adminController = new AdminController(adminServiceMock, navigationServiceMock, sessionMock);

        // Mock JavaFX components
        adminController.userTable = new TableView<>();
        adminController.eventTable = new TableView<>();
        adminController.searchUserField = new TextField();
    }

    @Test
    void testLoadUsers() {
        List<User> mockUsers = List.of(new User(1, "testUser", "Test User", "test@example.com"));
        when(adminServiceMock.findAllUsers()).thenReturn(mockUsers);

        adminController.loadUsers();

        Assertions.assertEquals(1, adminController.userTable.getItems()
                .size());
        verify(adminServiceMock, times(1)).findAllUsers();
    }

    @Test
    void testHandleCreateUser() {
        when(navigationServiceMock.showDialog(anyString(), eq(CreateUserDialogController.class), any()))
                .thenReturn(mock(CreateUserDialogController.class));

        adminController.handleCreateUser();

        verify(navigationServiceMock, times(1)).showDialog(anyString(), eq(CreateUserDialogController.class), any());
    }

    @Test
    void testHandleDeleteEvent() {
        Event mockEvent = new Event(1, "Test Event");
        adminController.eventTable.getItems()
                .add(mockEvent);
        adminController.eventTable.getSelectionModel()
                .select(mockEvent);

        ConfirmDeleteEventController deleteControllerMock = mock(ConfirmDeleteEventController.class);
        when(deleteControllerMock.isDeleted()).thenReturn(true);
        when(navigationServiceMock.showDialog(anyString(), eq(ConfirmDeleteEventController.class), any()))
                .thenReturn(deleteControllerMock);

        adminController.handleDeleteEvent();

        verify(navigationServiceMock, times(1)).showDialog(anyString(), eq(ConfirmDeleteEventController.class), any());
        verify(adminServiceMock, times(1)).findAllEvents();
    }
}
