package org.example.eventticketsystem.utils;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class ContentViewUtils {
    private static StackPane contentArea;

    public static void setContentArea(StackPane area) {
        contentArea = area;
    }

    public static void setContent(Node node) {
        if (contentArea != null) {
            contentArea.getChildren().setAll(node);
        } else {
            System.err.println("❌ Content area not initialized.");
        }
    }
}