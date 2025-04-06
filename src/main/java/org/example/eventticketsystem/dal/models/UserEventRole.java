package org.example.eventticketsystem.dal.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/* Even though it’s a join table, modeling it explicitly is best practice'
 when we might extend it later, or we need to edit it via GUI */
public class UserEventRole {
    private int userId;
    private int eventId;
    private String role;
}
