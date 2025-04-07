package org.example.eventticketsystem.dal.models;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Identifiable {
    private int id;
    private String title;
    private String description;
    private String locationGuidance;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private int capacity;
    private boolean isPublic;
}
