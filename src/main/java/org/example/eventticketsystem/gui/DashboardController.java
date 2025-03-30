package org.example.eventticketsystem.gui;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.bll.TicketService;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.utils.INavigation;
//import org.example.eventticketsystem.services.TicketService;
import org.example.eventticketsystem.bll.EventService;


import java.util.Map;


public class DashboardController extends BaseController<User> {

    @FXML
    private Label dashboardTitle;
    @FXML
    private VBox adminSection;
    @FXML
    private VBox coordinatorSection;

    // Admin Stats UI
    @FXML
    private Label usersCountLabel;
    @FXML
    private Label eventsCountLabel;
    @FXML
    private Label ticketsCountLabel;
    @FXML
    private Button refreshStatsButton;

    @FXML private BarChart<String, Number> ticketSalesChart;
    @FXML private CategoryAxis monthAxis;
    @FXML private NumberAxis salesAxis;

    private final TicketService ticketService;  // optional
    private final EventService eventService;    // optional

    public DashboardController(INavigation navigation, UserService userService) {
        super(navigation, userService);
        this.eventService = null;
        this.ticketService = null;
    }


    /**
     * Initializes the dashboard controller.
     */

    @FXML
    public void initialize() {
        User user = navigation.getCurrentUser();

        if (user == null) {
            adminSection.setVisible(false);
            coordinatorSection.setVisible(false);
            dashboardTitle.setText("No logged in.");
            return;
        }

        String role = user.getRole();

        switch (role) {
            case "ADMIN" -> {
                adminSection.setVisible(true);
                coordinatorSection.setVisible(false);
                dashboardTitle.setText("Admin Oversigt");
                loadAdminStats();
                loadTicketSalesChart();
            }
            case "EVENT_COORDINATOR" -> {
                adminSection.setVisible(false);
                coordinatorSection.setVisible(true);
                dashboardTitle.setText("Arrangement Oversigt");
            }
            default -> {
                adminSection.setVisible(false);
                coordinatorSection.setVisible(false);
                dashboardTitle.setText("Ukendt rolle: " + role);
            }
        }
    }

    @FXML
    private void handleRefreshStats() {
        loadAdminStats();
        loadTicketSalesChart();
    }

    /**
     * Example method that loads stats from services and updates admin labels.
     */
    private void loadAdminStats() {
        usersCountLabel.setText("Brugere: " + userService.getAllUsers().size());
        assert eventService != null;
        eventsCountLabel.setText("Events: " + eventService.getAllEvents().size());
        assert ticketService != null;
        ticketsCountLabel.setText("Billetter: " + ticketService.getTicketCount());
    }

    private void loadTicketSalesChart() {
        ticketSalesChart.getData().clear();

        Map<String, Integer> monthlySales = ticketService.getMonthlyTicketSales(); // placeholder for real logic

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Billetsalg 2025");

        for (Map.Entry<String, Integer> entry : monthlySales.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        ticketSalesChart.getData().add(series);
    }
}