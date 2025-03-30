package org.example.eventticketsystem.gui.coordinator;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;

public class CoordinatorDashboardController {
    @FXML
    private Label eventCountLabel;
    @FXML private Label ticketsSoldLabel;
    @FXML private BarChart<String, Number> eventSalesChart;
    @FXML private CategoryAxis eventAxis;
    @FXML private NumberAxis salesAxis;

    // + other @FXML bindings...

    @FXML
    public void initialize() {
        // TODO: Load data for this coordinator only
    }
}