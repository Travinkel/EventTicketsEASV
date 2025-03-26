package org.example.eventticketsystem.models;

public class Ticket {
    private int id;
    private String eventId;
    private String userId;
    private String qrCode;

    public Ticket(int id, String eventId, String userId, String qrCode) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.qrCode = qrCode;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getQrCode() {
        return qrCode;
    }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Class<Object> getEvent() {
        return Object.class;
    }
}
