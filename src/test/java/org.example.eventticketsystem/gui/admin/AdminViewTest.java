package org.example.eventticketsystem.gui.admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminViewTest extends ApplicationTest {

    private AdminController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/eventticketsystem/gui/admin/AdminView.fxml"));
        VBox root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @BeforeEach
    void setup() {
        // Setup, hvis nødvendigt
    }

    @Test
    void testAllUIElementsLoaded() {
        assertNotNull(lookup("#searchUserField").queryAs(TextField.class));
        assertNotNull(lookup("#roleFilterComboBox").queryAs(ComboBox.class));
        assertNotNull(lookup("#createUserButton").queryAs(Button.class));
        assertNotNull(lookup("#editUserButton").queryAs(Button.class));
        assertNotNull(lookup("#deleteUserButton").queryAs(Button.class));
        assertNotNull(lookup("#userTable").queryAs(TableView.class));
        assertNotNull(lookup("#assignCoordinatorButton").queryAs(Button.class));
        assertNotNull(lookup("#deleteEventButton").queryAs(Button.class));
        assertNotNull(lookup("#eventTable").queryAs(TableView.class));
    }

    @Test
    void testUserTableHasCorrectColumns() {
        TableView<?> table = lookup("#userTable").queryTableView();

        assertEquals("Brugernavn", table.getColumns()
                .get(0)
                .getText());
        assertEquals("Navn", table.getColumns()
                .get(1)
                .getText());
        assertEquals("Email", table.getColumns()
                .get(2)
                .getText());
        assertEquals("Roller", table.getColumns()
                .get(3)
                .getText());
        assertEquals("Events", table.getColumns()
                .get(4)
                .getText());
    }

    @Test
    void testEventTableHasCorrectColumns() {
        TableView<?> table = lookup("#eventTable").queryTableView();

        assertEquals("Titel", table.getColumns()
                .get(0)
                .getText());
        assertEquals("Start", table.getColumns()
                .get(1)
                .getText());
        assertEquals("Slut", table.getColumns()
                .get(2)
                .getText());
        assertEquals("Koordinatorer", table.getColumns()
                .get(3)
                .getText());
    }

    @Test
    void testSearchFieldInput() {
        TextField searchField = lookup("#searchUserField").queryAs(TextField.class);
        clickOn(searchField).write("Testbruger");
        assertEquals("Testbruger", searchField.getText());
    }
}
