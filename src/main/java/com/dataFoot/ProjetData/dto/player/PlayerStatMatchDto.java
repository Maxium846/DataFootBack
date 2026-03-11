package com.dataFoot.ProjetData.dto.player;

import lombok.Data;
@Data
public class PlayerStatMatchDto {

        private Long id;
        private long playerId;

        private long matchId;
        private long club;
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


