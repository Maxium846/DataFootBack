package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@ToString(exclude = {"clubs", "classements"})
@Table(name = "championnats")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class League {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;
    private String country;

    // Relation avec clubs: une league contient plusieurs  ligne club et chaque club appartient  a une ligue
    @OneToMany(mappedBy = "league", cascade = CascadeType.REMOVE)
    private List<Club> clubs = new ArrayList<>();

    // Relation avec Classement : Une ligue contient plusieurs LIGNES de classement et chaque ligne de classement appartient a une seul ligue
    @OneToMany(mappedBy = "league")
    private List<Classement> classements = new ArrayList<>();

    @OneToMany(mappedBy = "league")
    private List<MatchLineUp> matchLineUps = new ArrayList<>();



}
