package com.dataFoot.ProjetData.dto.player.playerStat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerStatImpactDto {

    // info Joueur
    private Long playerId;
    private String name;
    private String clubName;
    private Long clubId;
    private String logo;
    //tacle

    private Long totalTackle;
    private Long blocks;
    private Long interception;


    //duels

    private Long totalDuels;
    private Long wonDuels;

    // Dribles

    private Long attemptsDribbles;
    private Long sucessDribles;
    private Long pastDribbles;

    public PlayerStatImpactDto(Long playerId, String name, String clubName, Long clubId, String logo, Long totalTackle, Long blocks, Long interception, Long totalDuels, Long wonDuels, Long attemptsDribbles, Long sucessDribles, Long pastDribbles) {
        this.playerId = playerId;
        this.name = name;
        this.clubName = clubName;
        this.clubId = clubId;
        this.logo = logo;
        this.totalTackle = totalTackle;
        this.blocks = blocks;
        this.interception = interception;
        this.totalDuels = totalDuels;
        this.wonDuels = wonDuels;
        this.attemptsDribbles = attemptsDribbles;
        this.sucessDribles = sucessDribles;
        this.pastDribbles = pastDribbles;
    }
}
