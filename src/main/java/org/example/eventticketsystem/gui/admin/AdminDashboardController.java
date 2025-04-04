package org.example.eventticketsystem.gui.admin;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.utils.INavigation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.example.eventticketsystem.services.TicketService;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Injectable
public class AdminDashboardController extends BaseController<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDashboardController.class);

    @FXML private Label usersCountLabel;

    @FXML private Label adminCountLabel;
    @FXML private Label coordinatorCountLabel;
    @FXML private PieChart roleDistributionChart;
    @FXML private Label lastLoginSummary;
    @FXML private Button refreshDashboardButton;
    @FXML private Label dbStatusLabel;
    @FXML private Label pingLabel;
    @FXML private Label connectionLabel;
    @FXML private Label lastUpdatedLabel;


    @FXML private BarChart<String, Number> roleBarChart;
    @FXML private CategoryAxis barChartXAxis;
    @FXML private NumberAxis barChartYAxis;

    public AdminDashboardController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @FXML
    public void initialize() {
        LOGGER.info("✅ AdminDashboardController initialized");

        if (!"ADMIN".equalsIgnoreCase(navigation.getCurrentUser().getRole())) return;

        refreshDashboardButton.setOnAction(e -> handleRefreshDashboard());
        handleRefreshDashboard();
    }


    @FXML
    private void handleRefreshStats() {
        populateUserStats();
        updateRoleDistribution();
        updateSystemStatus();
    }

    @FXML
    private void handleRefreshDashboard() {
        LOGGER.debug("Refreshing dashboard stats...");

        populateUserStats();
        updateRoleDistribution();
        updateSystemStatus();
    }

    private void populateUserStats() {
        long totalUsers = userService.getTotalUserCount();
        long adminCount = userService.countByRole("ADMIN");
        long coordinatorCount = userService.countByRole("EVENT_COORDINATOR");

        usersCountLabel.setText("Brugere: " + totalUsers);
        adminCountLabel.setText("Admins: " + adminCount);
        coordinatorCountLabel.setText("Koordinatorer: " + coordinatorCount);
    }

    private void updateRoleDistribution() {
        roleDistributionChart.getData().clear();

        Map<String, Long> roleCounts = userService.countUsersByRole();
        for (Map.Entry<String, Long> entry : roleCounts.entrySet()) {
            roleDistributionChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }

    private void updateSystemStatus() {
        try {
            long start = System.currentTimeMillis();
            userService.getAllUsers(); // ping DB
            long duration = System.currentTimeMillis() - start;

            dbStatusLabel.setText("🟢 Database Online");
            pingLabel.setText("Ping: " + duration + " ms");
            connectionLabel.setText("Forbindelser: N/A");
        } catch (Exception e) {
            dbStatusLabel.setText("🔴 Database Offline");
            pingLabel.setText("Ping: ❌");
            connectionLabel.setText("Forbindelser: ❌");
        }
        lastUpdatedLabel.setText("Opdateret: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
    }
}