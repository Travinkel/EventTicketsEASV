package org.example.eventticketsystem.models;

import java.util.Date;

public class Event {
    private int id;
    private String name;
    private Date date;
    private String location;
    private String notes;

    public Event(int id, String name, Date date, String location, String notes) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.notes = notes;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
