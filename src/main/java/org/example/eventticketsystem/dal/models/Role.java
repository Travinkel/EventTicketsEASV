package org.example.eventticketsystem.dal.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "name"})
public class Role {
    private int id;
    private String name;
}
