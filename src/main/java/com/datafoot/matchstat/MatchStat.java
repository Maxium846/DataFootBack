package com.datafoot.matchstat;

import com.datafoot.match.Match;
import com.datafoot.team.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "match_team_stats", uniqueConstraints = {
        @UniqueConstraint(name = "uk_match_team", columnNames = {"match_id", "team_id"})
})

public class MatchStat {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
    private Integer shootsOnGoals;
    private Integer shootOffGoals;
    private Integer totalShots;
    private Integer blockedShots;
    private Integer shotInsideBox;
    private Integer shotsOutsideBox;
    private Integer fouls;
    private Integer cornerKick;
    private Integer offsides;
    private Integer ballPossession;
    private Integer yellowCards;
    private Integer redCards;
    private Integer goalkeeperSave;
    private Integer totalPasses;
    private Integer passesAccurate;
    private Integer passesPercentage;
    private Double expectedGoals;
    private Double goalsPrevented;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id")
    private Match match;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private Team teamId;




}
