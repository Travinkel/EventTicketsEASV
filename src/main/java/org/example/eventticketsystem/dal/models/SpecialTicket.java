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
public class SpecialTicket {
    private int id;
    private Integer userId;
    private Integer eventId;
    private String type;
    private String qrCode;
    private String barcode;
    private int issuedBy;
    private LocalDateTime createdAt;
}
