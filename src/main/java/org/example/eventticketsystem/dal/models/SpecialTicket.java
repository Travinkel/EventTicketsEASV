package org.example.eventticketsystem.dal.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"userId", "eventId", "type"})
public class SpecialTicket {
    private int
            id;
    private Integer
            userId;
    private Integer
            eventId;
    private String
            type;
    private int
            issuedBy;
    private LocalDateTime
            createdAt =
            LocalDateTime.now();
    private String
            qrCode;
    private String
            barcode;
}
