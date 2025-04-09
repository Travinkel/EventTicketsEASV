package org.example.eventticketsystem.gui.coordinator;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.eventticketsystem.bll.services.EventCoordinatorService;
import org.example.eventticketsystem.bll.services.interfaces.INavigationService;
import org.example.eventticketsystem.bll.session.SessionContext;
import org.example.eventticketsystem.bll.viewmodels.TicketComposite;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.SpecialTicket;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.mail.EmailService;
import org.example.eventticketsystem.utils.pdf.PDFGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@Injectable
public class EventCoordinatorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventCoordinatorController.class);

    private final EventCoordinatorService coordinatorService;
    private final SessionContext session;
    private final EmailService emailService;
    private final INavigationService navigationService;
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
    private TableView<TicketComposite> ticketTable;
    @FXML
    private TableColumn<TicketComposite, String> colTicketId;
    @FXML
    private TableColumn<TicketComposite, String> colUserId;
    @FXML
    private TableColumn<TicketComposite, String> colEmail;
    @FXML
    private TableColumn<TicketComposite, String> colPrice;
    @FXML
    private TableColumn<TicketComposite, String> colCheckedIn;
    @FXML
    private Button btnSendTicket;
    @FXML
    private Button btnCreateEvent;
    @FXML
    private Button btnEditEvent;
    @FXML
    private Button btnDeleteEvent;
    @FXML
    private Button btnAssignCoordinator;
    @FXML
    private Button btnCreateTicket;
    @FXML
    private Button btnDeleteTicket;
    @FXML
    private Button btnCreateSpecialTicket;
    @FXML
    private TextField txtSpecialTitle;
    @FXML
    private TextField txtSpecialEventId;

    public EventCoordinatorController(EventCoordinatorService coordinatorService, SessionContext session,
                                      EmailService emailService, INavigationService navigationService) {
        this.coordinatorService = coordinatorService;
        this.session = session;
        this.emailService = emailService;
        this.navigationService = navigationService;

        LOGGER.debug("🧩 EventCoordinatorController instantiated (Session ID: {})", session.hashCode());
    }

    @FXML
    public void initialize() {
        LOGGER.info("📋 Initializing EventCoordinatorController");
        setupEventTable();
        setupTicketTable();
        setupActions();
        loadCoordinatorEvents();
        LOGGER.info("✅ EventCoordinatorController initialization complete");
    }

    private void setupEventTable() {
        colTitle.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue()
                .getTitle()));
        colStartTime.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue()
                .getStartTime()
                .toString()));
        colEndTime.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue()
                .getEndTime()
                .toString()));
        colLocation.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue()
                .getLocationGuidance()));
    }

    private void setupTicketTable() {
        colTicketId.setCellValueFactory(t -> new ReadOnlyStringWrapper(String.valueOf(t.getValue()
                .getTicketId())));
        colUserId.setCellValueFactory(t -> new ReadOnlyStringWrapper(String.valueOf(t.getValue()
                .getUserId())));
        colEmail.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue()
                .getUserEmail()));
        colPrice.setCellValueFactory(t -> new ReadOnlyStringWrapper(String.format("%.2f", t.getValue()
                .getPrice())));
        colCheckedIn.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue()
                .isCheckedIn() ? "Ja" : "Nej"));
    }

    private void setupActions() {
        eventTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldEvent, newEvent) -> {
                    if (newEvent != null) {
                        loadTicketsForEvent(newEvent.getId());
                    } else {
                        ticketTable.getItems()
                                .clear();
                    }
                });

        btnSendTicket.setOnAction(e -> handleSendTicket());

        btnCreateEvent.setOnAction(e -> handleCreateEvent());
        btnEditEvent.setOnAction(e -> handleEditEvent());
        btnDeleteEvent.setOnAction(e -> handleDeleteEvent());
        btnAssignCoordinator.setOnAction(e -> handleAssignCoordinator());

        btnCreateTicket.setOnAction(e -> handleCreateTicket());
        btnDeleteTicket.setOnAction(e -> handleDeleteTicket());

        btnCreateSpecialTicket.setOnAction(e -> handleCreateSpecialTicket());
    }

    private void loadCoordinatorEvents() {
        int userId = session.getCurrentUser()
                .getId();
        List<Event> events = coordinatorService.getEventsForCoordinator(userId);
        eventTable.getItems()
                .setAll(events);
        LOGGER.info("✅ Loaded {} events for coordinator {}", events.size(), userId);

        // Vælg første event automatisk:
        if (!events.isEmpty()) {
            eventTable.getSelectionModel()
                    .selectFirst(); // 👈 Dette vil trigger din listener
        }
    }

    private void loadTicketsForEvent(int eventId) {
        List<TicketComposite> tickets = coordinatorService.findTicketsForEvent(eventId);
        LOGGER.debug("🔍 Loading tickets for event ID: {}", eventId);
        for (TicketComposite ticket : tickets) {
            LOGGER.debug("Ticket loaded: {} - {}", ticket.getTicketId(), ticket.getUserEmail());
        }
        ticketTable.getItems()
                .setAll(tickets);
        LOGGER.info("✅ Loaded {} tickets for event {}", tickets.size(), eventId);
    }

    private void handleSendTicket() {
        TicketComposite selected = ticketTable.getSelectionModel()
                .getSelectedItem();
        if (selected == null) {
            showAlert("Vælg en billet", "Du skal vælge en billet for at sende den.");
            return;
        }

        Event selectedEvent = eventTable.getSelectionModel()
                .getSelectedItem();
        if (selected == null || selectedEvent == null) {
            showAlert("Vælg en billet og et event", "Du skal vælge både en billet og et event.");
            return;
        }

        try {
            String pdfPath = PDFGenerator.generate(selected.getTicket(), selectedEvent);
            File file = new File(pdfPath);
            byte[] pdfBytes = java.nio.file.Files.readAllBytes(file.toPath());

            emailService.sendEmailWithAttachment(
                    selected.getUserEmail(),
                    "Din billet",
                    "Her er din billet som PDF.",
                    pdfBytes,
                    "billet.pdf"
            );

            showAlert("Sendt!", "Billet sendt til " + selected.getUserEmail());
        } catch (Exception ex) {
            LOGGER.error("❌ Kunne ikke sende billet", ex);
            showAlert("Fejl", "Kunne ikke sende billetten.");
        }
    }

    private void handleCreateEvent() {
        CreateEventDialogController controller = navigationService.showDialog(
                "/views/coordinator/CreateEventDialog.fxml",
                CreateEventDialogController.class,
                c -> LOGGER.debug("🧩 Injecting CreateEventDialogController")
        );

        if (controller != null && controller.isEventCreated()) {
            loadCoordinatorEvents();
            showAlert("Event oprettet", "Det nye event blev oprettet.");
        }
    }

    private void handleEditEvent() {
        Event selectedEvent = eventTable.getSelectionModel()
                .getSelectedItem();
        if (selectedEvent == null) {
            showAlert("Ingen valgt", "Vælg et event du vil redigere.");
            return;
        }

        EditEventDialogController controller = navigationService.showDialog(
                "/views/coordinator/EditEventDialog.fxml",
                EditEventDialogController.class,
                c -> {
                    c.setEvent(selectedEvent);
                    c.populateFields();
                });

        if (controller != null && controller.isEventUpdated()) {
            loadCoordinatorEvents();
            showAlert("Event opdateret", "Eventet blev opdateret.");
        }
    }


    private void handleDeleteEvent() {
        Event selected = eventTable.getSelectionModel()
                .getSelectedItem();
        if (selected == null) {
            showAlert("Vælg et event", "Du skal vælge et event for at slette det.");
            return;
        }

        boolean deleted = coordinatorService.deleteEvent(selected.getId(), session.getCurrentUser()
                .getId());
        if (deleted) {
            loadCoordinatorEvents();
            showAlert("Slettet", "Event blev slettet.");
        } else {
            showAlert("Fejl", "Kunne ikke slette eventet.");
        }
    }

    private void handleAssignCoordinator() {
        Event selectedEvent = eventTable.getSelectionModel()
                .getSelectedItem();
        if (selectedEvent == null) {
            showAlert("Vælg event", "Du skal vælge et event for at tilføje en koordinator.");
            return;
        }

        AssignCoordinatorDialogController controller = navigationService.showDialog(
                "/views/admin/AssignCoordinatorDialog.fxml", // 🧠 kan genbruges!
                AssignCoordinatorDialogController.class,
                c -> c.setEvent(selectedEvent)
        );

        if (controller != null && controller.isCoordinatorAssigned()) {
            loadCoordinatorEvents();
            showAlert("Koordinator tildelt", "Koordinatoren er nu tilknyttet.");
        }
    }

    private void handleCreateTicket() {
        Event selectedEvent = eventTable.getSelectionModel()
                .getSelectedItem();
        if (selectedEvent == null) {
            showAlert("Vælg event", "Du skal vælge et event.");
            return;
        }


        CreateTicketDialogController controller = navigationService.showDialog(
                "/views/coordinator/CreateTicketDialog.fxml",
                CreateTicketDialogController.class,
                c -> c.setEvent(selectedEvent)
        );

        if (controller != null && controller.isTicketCreated()) {
            loadTicketsForEvent(selectedEvent.getId());
            showAlert("Billet oprettet", "Billet er oprettet og klar til brug.");
        }
    }

    private void handleDeleteTicket() {
        TicketComposite selected = ticketTable.getSelectionModel()
                .getSelectedItem();
        if (selected == null) {
            showAlert("Vælg en billet", "Du skal vælge en billet for at slette den.");
            return;
        }

        boolean deleted = coordinatorService.deleteTicket(selected.getTicketId());
        if (deleted) {
            loadTicketsForEvent(selected.getTicket()
                    .getEventId());
            showAlert("Slettet", "Billet blev slettet.");
        } else {
            showAlert("Fejl", "Kunne ikke slette billetten.");
        }
    }

    private void handleCreateSpecialTicket() {
        String title = txtSpecialTitle.getText();
        String eventIdText = txtSpecialEventId.getText();

        if (title == null || title.isBlank()) {
            showAlert("Fejl", "Titel på specialbillet skal udfyldes.");
            return;
        }

        int eventId = -1;
        if (!eventIdText.isBlank()) {
            try {
                eventId = Integer.parseInt(eventIdText);
            } catch (NumberFormatException e) {
                showAlert("Fejl", "Event ID skal være et tal.");
                return;
            }
        }

        SpecialTicket specialTicket = new SpecialTicket();
        specialTicket.setType(title);
        specialTicket.setEventId(eventId > 0 ? eventId : null); // null hvis global

        boolean created = coordinatorService.issueSpecialTicket(specialTicket);
        if (created) {
            showAlert("Oprettet", "Specialbillet oprettet.");
            txtSpecialTitle.clear();
            txtSpecialEventId.clear();
        } else {
            showAlert("Fejl", "Kunne ikke oprette specialbillet.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
