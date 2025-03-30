package org.example.eventticketsystem.utils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class ContentViewUtils {
    private static StackPane contentArea;
    private static Scene mainScene;
    private static String currentTheme;

    public static void setContentArea(StackPane area) {
        contentArea = area;
    }

    public static void setMainScene(Scene scene) {
        mainScene = scene;
    }

    public static void setContent(Node node) {
        if (contentArea != null) {
            contentArea.getChildren().setAll(node);
        } else {
            System.err.println("❌ Content area not initialized.");
        }
    }

    public static void setTheme(String cssFileName) {
        if (mainScene == null) {
            System.err.println("❌ Scene not set — call setMainScene() on app startup.");
            return;
        }

        // Remove any old theme first
        if (currentTheme != null) {
            mainScene.getStylesheets().removeIf(sheet -> sheet.endsWith(currentTheme));
        }

        // Load new theme
        String themePath = Objects.requireNonNull(ContentViewUtils.class.getResource("/styles/" + cssFileName)).toExternalForm();
        mainScene.getStylesheets().add(themePath);
        currentTheme = cssFileName;

        System.out.println("✅ Applied theme: " + cssFileName);
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }
}