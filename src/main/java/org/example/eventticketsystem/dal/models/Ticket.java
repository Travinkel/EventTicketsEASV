package org.example.eventticketsystem.dal.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"eventId", "userId", "qrCode"})
public class Ticket {
    private int id;
    private int eventId;
    private int userId;
    private LocalDateTime issuedAt;
    private boolean checkedIn;
    private double priceAtPurchase;
    private String qrCode;
    private String barcode;
}
