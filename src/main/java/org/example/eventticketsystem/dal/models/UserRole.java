package org.example.eventticketsystem.dal.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"userId", "roleId"})
public class UserRole {
    private int userId;
    private int roleId;
}
