package com.dataFoot.ProjetData.model;

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
    private Club club;

    private String nameClub;
    private String nameJoueur;
    // Match
    private int minutePlayed;
    private String note;
    private boolean captain;
    private boolean substitute;
    private int offside;

    // Shoot

    private int totalShoot;
    private int shootOnTarget;

    // Goal

    private int totalGoal;
    private int goalConceded;
    private int assist;
    private int saves;

    // passes

    private int totalPasse;
    private int keyPasse;
    private String accuracyPass;

    //tacle

    private int totalTackle;
    private int blocks;
    private int interception;


    //duels

    private int totalDuels;
    private int wonDuels;

    // Dribles

    private int attemptsDribbles;
    private int sucessDribles;
    private int pastDribbles;


    // fouls
    private int foulsDrawns;
    private int foulsCommitted;


    // Carton

    private int yellowCard;
    private int redCard;


    //penalty

    private int penaltyWon;
    private int penaltyCommited;
    private int penaltyScored;
    private int penaltyMissed;
    private int penaltSaved;
}

