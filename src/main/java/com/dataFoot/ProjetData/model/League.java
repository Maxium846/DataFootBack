package com.dataFoot.ProjetData.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@ToString(exclude = {"clubs", "classements"})
@Table(name = "championnats")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class League {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;
    private String country;

    // Relation avec clubs: une league contient plusieurs  ligne club et chaque club appartient  a une ligue
    @OneToMany(mappedBy = "league")
    private List<Club> clubs = new ArrayList<>();

    @OneToMany(mappedBy = "league")
    private List<Classement> classements = new ArrayList<>();

    private Integer apiFootballLeague;




}
