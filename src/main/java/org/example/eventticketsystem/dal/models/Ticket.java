package org.example.eventticketsystem.dal.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Lombok boilerplate code (lombok added to dependencies)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    protected int id;
    private int eventId;
    private int userId;
    private String qrCode;
    private String barCode;
    private LocalDateTime issuedAt;
    private boolean checkedIn;
    private double priceAtPurchase;
}
