package org.example.eventticketsystem.controllers;

import javafx.scene.Node;
import org.example.eventticketsystem.utils.ContentViewUtils;
import org.example.eventticketsystem.utils.INavigation;

public interface IViewUpdater {
    void updateMainContent(Node content);
}