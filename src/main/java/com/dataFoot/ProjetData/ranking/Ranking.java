package com.dataFoot.ProjetData.ranking;

import com.dataFoot.ProjetData.league.League;
import com.dataFoot.ProjetData.team.Teams;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ranking",
        uniqueConstraints = @UniqueConstraint(columnNames = {"league_id", "club_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Teams teams;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    private int points;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
}
