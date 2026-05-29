package com.dataFoot.ProjetData.league;

import com.dataFoot.ProjetData.ranking.Ranking;
import com.dataFoot.ProjetData.team.Teams;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@ToString(exclude = {"teams", "rankings"})
@Table(name = "competition")
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

    @OneToMany(mappedBy = "league")
    private List<Teams> teams = new ArrayList<>();

    @OneToMany(mappedBy = "league")
    private List<Ranking> rankings = new ArrayList<>();

    private Integer apiFootballLeague;
    private String flag;
    private String logo;


}
