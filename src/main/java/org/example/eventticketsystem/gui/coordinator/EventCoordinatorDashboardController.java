package org.example.eventticketsystem.gui.coordinator;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.eventticketsystem.bll.EventService;
import org.example.eventticketsystem.bll.TicketService;
import org.example.eventticketsystem.bll.UserService;
import org.example.eventticketsystem.di.Injectable;
import org.example.eventticketsystem.models.User;
import org.example.eventticketsystem.gui.BaseController;
import org.example.eventticketsystem.utils.INavigation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Injectable
public class EventCoordinatorDashboardController extends BaseController<User> {

    private static final Logger logger = LoggerFactory.getLogger(EventCoordinatorDashboardController.class);


    @FXML private Label eventCountLabel;
    @FXML private Label totalTicketsLabel;
    @FXML private PieChart ticketDistributionChart;

    @FXML private BarChart<String, Number> revenueBarChart;
    @FXML private CategoryAxis revenueXAxis;
    @FXML private NumberAxis revenueYAxis;

    @FXML private Label dbStatusLabel;
    @FXML private Label pingLabel;
    @FXML private Label lastUpdatedLabel;
    @FXML private Button refreshDashboardButton;

    private final EventService eventService;
    private final TicketService ticketService;

    public EventCoordinatorDashboardController(INavigation navigation,
                                               UserService userService,
                                               EventService eventService,
                                               TicketService ticketService) {
        super(navigation, userService);
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @FXML
    public void initialize() {
        logger.info("✅ EventCoordinatorDashboardController initialized");

        User current = navigation.getCurrentUser();
        if (current == null || !"EVENT_COORDINATOR".equalsIgnoreCase(current.getRole())) return;

        populateEventStats(current.getId());
        updateTicketChart(current.getId());
        updateRevenueChart(current.getId());
        updateSystemStatus();

        refreshDashboardButton.setOnAction(e -> {
            populateEventStats(current.getId());
            updateTicketChart(current.getId());
            updateRevenueChart(current.getId());
            updateSystemStatus();
        });
    }

    private void populateEventStats(int coordinatorId) {
        long totalEvents = eventService.countEventsByCoordinator(coordinatorId);
        long totalTickets = ticketService.countTicketsByCoordinator(coordinatorId);

        eventCountLabel.setText("Events: " + totalEvents);
        totalTicketsLabel.setText("Solgte billetter: " + totalTickets);
    }

    private void updateTicketChart(int coordinatorId) {
        ticketDistributionChart.getData().clear();
        Map<String, Long> ticketsPerEvent = ticketService.getTicketCountPerEvent(coordinatorId);
        ticketsPerEvent.forEach((event, count) ->
                ticketDistributionChart.getData().add(new PieChart.Data(event, count))
        );
    }

    private void updateRevenueChart(int coordinatorId) {
        revenueBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Omsætning");

        Map<String, Double> revenuePerEvent = ticketService.getRevenuePerEvent(coordinatorId);
        revenuePerEvent.forEach((event, revenue) ->
                series.getData().add(new XYChart.Data<>(event, revenue))
        );

        revenueBarChart.getData().add(series);
    }

    private void updateSystemStatus() {
        try {
            long start = System.currentTimeMillis();
            eventService.getAllEvents(); // Pings DB
            long ping = System.currentTimeMillis() - start;
            dbStatusLabel.setText("🟢 Database Online");
            pingLabel.setText("Ping: " + ping + " ms");
        } catch (Exception e) {
            dbStatusLabel.setText("🔴 Database Offline");
            pingLabel.setText("Ping: ❌");
        }

        lastUpdatedLabel.setText("Opdateret: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
    }
}
