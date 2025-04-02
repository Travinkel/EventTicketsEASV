package org.example.eventticketsystem.models;


import java.time.LocalDateTime;

public interface Auditable {
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime dt);
}
