package org.example.eventticketsystem.models;

import java.time.LocalDateTime;

public class Ticket extends BaseEntity {
    private int eventId;
    private int userId;
    private String qrCode;
    private LocalDateTime issuedAt;

    public Ticket(int id, int eventId, int userId, String qrCode, LocalDateTime issuedAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.qrCode = qrCode;
        this.issuedAt = issuedAt;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getQrCode() {
        return qrCode;
    }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }


    public Class<Object> getEvent() {
        return Object.class;
    }
}
