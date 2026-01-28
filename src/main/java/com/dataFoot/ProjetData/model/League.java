package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = {"clubs", "classements"})
@Table(name = "championnats")
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;

    // Relation avec clubs
    @OneToMany(mappedBy = "league", cascade = CascadeType.REMOVE)
    private List<Club> clubs = new ArrayList<>();

    // Relation avec Classement
    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<Classement> classements = new ArrayList<>();
}
