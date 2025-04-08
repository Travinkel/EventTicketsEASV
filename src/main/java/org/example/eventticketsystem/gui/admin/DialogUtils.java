package org.example.eventticketsystem.gui.admin;

import javafx.scene.control.Alert;

public class DialogUtils {

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
