package org.example.eventticketsystem.models;

import java.time.LocalDateTime;
import java.util.Date;

public class Event extends BaseEntity {
    private String name;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int coordinatorId;
    private double price;
    private boolean isPublic;

    public Event(int id, String name, String description, String location,
                 LocalDateTime startTime, LocalDateTime endTime,
                 int coordinatorId, double price, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.coordinatorId = coordinatorId;
        this.price = price;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public int getCoordinatorId() {
        return coordinatorId;
    }
    public void setCoordinatorId(int coordinatorId) {
        this.coordinatorId = coordinatorId;
    }
    public Class<Object> getCoordinator() {
        return Object.class;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public String getTitle() {
        return name; // or return a formatted version if needed
    }

}
