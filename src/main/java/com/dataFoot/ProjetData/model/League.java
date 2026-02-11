package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@ToString(exclude = {"clubs", "classements"})
@Table(name = "championnats")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

    public League(Long id, String name, String country, List<Club> clubs, List<Classement> classements) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.clubs = clubs;
        this.classements = classements;
    }

    public League() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Club> getClubs() {
        return clubs;
    }

    public void setClubs(List<Club> clubs) {
        this.clubs = clubs;
    }

    public List<Classement> getClassements() {
        return classements;
    }

    public void setClassements(List<Classement> classements) {
        this.classements = classements;
    }


}
