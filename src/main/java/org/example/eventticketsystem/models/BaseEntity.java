package org.example.eventticketsystem.models;

import java.time.LocalDateTime;

public abstract class BaseEntity {

    protected int id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
