package org.example.eventticketsystem.gui.admin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.eventticketsystem.dal.connection.DBConnection;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.dal.models.User;
import org.example.eventticketsystem.bll.services.UserService;
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
    @FXML private Circle dbStatusIndicator;


    @FXML private BarChart<String, Number> roleBarChart;
    @FXML private CategoryAxis barChartXAxis;
    @FXML private NumberAxis barChartYAxis;

    public AdminDashboardController(INavigation navigation, UserService userService) {
        super(navigation, userService);
    }

    @FXML
    public void initialize() {
        LOGGER.info("✅ AdminDashboardController initialized");

        Timeline refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> handleRefreshDashboard())
        );
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();

        refreshDashboardButton.setOnAction(e -> handleRefreshDashboard());
        refreshDashboardButton.setTooltip(new Tooltip("Opdater systemstatus og statistik"));

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

            dbStatusIndicator.setFill(Color.GREEN);
            dbStatusLabel.setText("Database Online");
            dbStatusLabel.setStyle("-fx-text-fill: green;");
            pingLabel.setText("Ping: " + duration + " ms");
            int connections = DBConnection.getActiveConnections();
            connectionLabel.setText("Forbindelser: " + (connections >= 0 ? connections : "❌"));
        } catch (Exception e) {
            dbStatusIndicator.setFill(Color.RED);
            dbStatusLabel.setText("Database Offline");
            dbStatusLabel.setStyle("-fx-text-fill: red;");
            pingLabel.setText("Ping: ❌");
            int connections = DBConnection.getActiveConnections();
            connectionLabel.setText("Forbindelser: " + (connections >= 0 ? connections : "❌"));
        }
        lastUpdatedLabel.setText("Opdateret: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
    }
}
