package com.dataFoot.ProjetData.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@ToString(exclude = "club")

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String name;
    private String position;
    private int age;

    @ManyToOne
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;
}
