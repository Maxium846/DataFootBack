package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDate matchDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "home_club_id", nullable = false)
    private Club homeClub;

    @ManyToOne(optional = false)
    @JoinColumn(name = "away_club_id", nullable = false)
    private Club awayClub;


    private Integer homeGoals;
    private Integer awayGoals;

    private boolean played;

    @ManyToOne(optional = false)
    private League league;

    @Column(nullable = false)
    private Integer journee;
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MatchLineUp> lineups = new ArrayList<>();
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MatchEvent> events = new ArrayList<>();
}
