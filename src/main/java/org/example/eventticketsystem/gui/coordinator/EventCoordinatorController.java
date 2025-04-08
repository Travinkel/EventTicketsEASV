package org.example.eventticketsystem.gui.coordinator;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.example.eventticketsystem.bll.services.EventCoordinatorService;
import org.example.eventticketsystem.bll.services.interfaces.IEventCoordinatorService;
import org.example.eventticketsystem.bll.services.EmailService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.dal.models.*;
import org.example.eventticketsystem.gui.viewmodels.TicketDisplayModel;
import org.example.eventticketsystem.utils.PDFGenerator;
import org.example.eventticketsystem.utils.di.Injectable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Injectable
public class EventCoordinatorController {

    @FXML
    private TableView<Event> eventTable;
    @FXML
    private TableColumn<Event, String> colTitle;
    @FXML
    private TableColumn<Event, String> colStartTime;
    @FXML
    private TableColumn<Event, String> colEndTime;
    @FXML
    private TableColumn<Event, String> colLocation;

    @FXML
    private TableView<TicketDisplayModel> ticketTable;
    @FXML
    private TableColumn<TicketDisplayModel, Integer> colTicketId;
    @FXML
    private TableColumn<TicketDisplayModel, Integer> colUserId;
    @FXML
    private TableColumn<TicketDisplayModel, String> colEmail;
    @FXML
    private TableColumn<TicketDisplayModel, Double> colPrice;
    @FXML
    private TableColumn<TicketDisplayModel, Boolean> colCheckedIn;

    @FXML
    private Button btnSendTicket;

    private final EventCoordinatorService coordinatorService;
    private final EmailService emailService;
    private final PDFGenerator pdfGenerator;

    private final ObservableList<Event> eventList = FXCollections.observableArrayList();
    private final ObservableList<TicketDisplayModel> ticketList = FXCollections.observableArrayList();

    private final SessionContext session;

    public EventCoordinatorController(
            EventCoordinatorService coordinatorService,
            EmailService emailService,
            PDFGenerator pdfGenerator,
            SessionContext session
    ) {
        this.coordinatorService = coordinatorService;
        this.emailService = emailService;
        this.pdfGenerator = pdfGenerator;
        this.session = session;
    }

    public void initialize() {
        setupTableColumns();
        loadAssignedEvents(session.getUserId());

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) loadTicketsForEvent(selected.getId());
        });

        btnSendTicket.setOnAction(e -> {
            TicketDisplayModel selected = ticketTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int ticketId = selected.getTicketId();
                int userId = selected.getUserId();

                // Load data using service
                Ticket ticket = coordinatorService.getTicketById(ticketId);
                User user = coordinatorService.getUserById(userId);
                Event event = eventTable.getSelectionModel().getSelectedItem();

                // Generate PDF using ticket + event (QR is already embedded)
                String filePath = pdfGenerator.generate(ticket, event);

                // Send the email using EmailService
                boolean sent = emailService.sendEmailWithAttachment(
                        user.getEmail(),
                        "Din billet til " + event.getTitle(),
                        "Kære " + user.getName() + ",\n\nVedhæftet finder du din billet til " + event.getTitle() + ".\nVis den venligst ved indgangen.\n\nMed venlig hilsen,\nEASV Bar",
                        filePath
                );

                if (sent) {
                    System.out.println("✅ Ticket sent to " + user.getEmail());
                } else {
                    System.err.println("❌ Failed to send ticket to " + user.getEmail());
                }
            }
        });
    }


    private void setupTableColumns() {
        colTitle.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTitle()));
        colStartTime.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getStartTime().toString()));
        colEndTime.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getEndTime().toString()));
        colLocation.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getLocationGuidance()));

        colTicketId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getTicketId()));
        colUserId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getUserId()));
        colEmail.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getEmail()));
        colPrice.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPrice()));
        colCheckedIn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().isCheckedIn()));

        eventTable.setItems(eventList);
        ticketTable.setItems(ticketList);
    }

    private void loadAssignedEvents(int userId) {
        eventList.setAll(coordinatorService.getEventsForCoordinator(session.getUserId()));
    }

    private void loadTicketsForEvent(int eventId) {
        ticketList.clear();
        List<Ticket> tickets = coordinatorService.getTicketsForEvent(eventId);
        for (Ticket ticket : tickets) {
            User user = coordinatorService.getUserById(ticket.getUserId());
            ticketList.add(new TicketDisplayModel(ticket, user));
        }
    }
}
