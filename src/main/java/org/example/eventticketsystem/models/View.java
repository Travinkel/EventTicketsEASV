package org.example.eventticketsystem.models;

public enum View {
    DASHBOARD("/views/AdminDashboardView.fxml"),
    ADMIN_DASHBOARD("/views/AdminDashboardView.fxml"),
    COORDINATOR_DASHBOARD("/views/DashboardView.fxml"),
    SIDEBAR_DASHBOARD("/views/SideBarDashboardView.fxml"),
    TICKET_VIEW("/views/TicketView.fxml"),
    LOGIN_VIEW("/views/LoginView.fxml"),
    USER_MANAGEMENT("/views/UserManagement.fxml"),
    TICKET_MANAGEMENT("/views/TicketManagement.fxml"),
    EVENT_MANAGEMENT("/views/EventManagement.fxml"),
    ADMIN_SETTINGS("/views/AdminSettingsView.fxml"),
    COORDINATOR_SETTINGS("/views/CoordinatorSettingsView.fxml");

    private final String path;

    View(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}