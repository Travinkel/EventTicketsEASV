package org.example.eventticketsystem.dal.models;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"username", "identityRole"})
public class User {
    private int id;
    private String username;
    private String hashedPassword;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}
