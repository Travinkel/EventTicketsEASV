package org.example.eventticketsystem.dal.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"userId", "role"})
public class UserRole {
    private int userId;
    private String role;
}
