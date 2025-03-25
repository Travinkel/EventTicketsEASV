package org.example.eventticketsystem.models;

public enum UserRole {
    ADMIN,
    COORDINATOR,
    CUSTOMER;

    @Override
    public String toString() {
        // Human-friendly display (optional)
        return switch (this) {
            case ADMIN -> "Admin";
            case COORDINATOR -> "Event Coordinator";
            case CUSTOMER -> "Customer";
        };
    }
}