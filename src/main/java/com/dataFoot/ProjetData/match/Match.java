package com.dataFoot.ProjetData.match;

import com.dataFoot.ProjetData.league.League;
import com.dataFoot.ProjetData.matchlineup.MatchLineUp;
import com.dataFoot.ProjetData.team.Teams;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDate matchDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "home_club_id", nullable = false)
    private Teams homeTeams;

    @ManyToOne(optional = false)
    @JoinColumn(name = "away_club_id", nullable = false)
    private Teams awayTeams;
    private Integer homeGoals;
    private Integer awayGoals;
    private boolean played;

    @ManyToOne(optional = false)
    @JoinColumn(name = "league_id",nullable = false)
    private League league;

    @Column(nullable = false)
    private Integer journee;
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MatchLineUp> lineups = new ArrayList<>();

    @Column(unique = true)
    private int apiFootballFixtureId;

}
