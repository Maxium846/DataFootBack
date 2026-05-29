package com.dataFoot.league;

import com.dataFoot.ranking.Ranking;
import com.dataFoot.team.Teams;
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
    @Column(nullable = false)
    private String country;

    @OneToMany(mappedBy = "league", fetch = FetchType.LAZY)
    private List<Teams> teams = new ArrayList<>();

    @OneToMany(mappedBy = "league",fetch = FetchType.LAZY)
    private List<Ranking> rankings = new ArrayList<>();

    @Column(nullable = false,unique = true)
    private Integer apiFootballLeagueId;
    private String flag;
    private String logo;



}
