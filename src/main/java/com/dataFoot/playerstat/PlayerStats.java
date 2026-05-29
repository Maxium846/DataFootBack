package com.dataFoot.playerstat;

import com.dataFoot.match.Match;
import com.dataFoot.player.Player;
import com.dataFoot.team.Teams;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "player")
@Table(name = "player_match_stats")
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Teams teams;

    private String nameClub;
    private String nameJoueur;
    // Match
    private Integer minutePlayed;
    private String note;
    private boolean captain;
    private boolean substitute;
    private Integer offside;

    // Shoot

    private Integer totalShoot;
    private Integer shootOnTarget;

    // Goal

    private Integer totalGoal;
    private Integer goalConceded;
    private Integer assist;
    private Integer saves;

    // passes

    private Integer totalPasse;
    private Integer keyPasse;
    private Integer accuracyPass;

    //tacle

    private Integer totalTackle;
    private Integer blocks;
    private Integer interception;


    //duels

    private Integer totalDuels;
    private Integer wonDuels;

    // Dribles

    private Integer attemptsDribbles;
    private Integer sucessDribles;
    private Integer pastDribbles;


    // fouls
    private Integer foulsDrawns;
    private Integer foulsCommitted;


    // Carton

    private Integer yellowCard;
    private Integer redCard;


    //penalty

    private Integer penaltyWon;
    private Integer penaltyCommited;
    private Integer penaltyScored;
    private Integer penaltyMissed;
    private Integer penaltSaved;
}

