package com.dataFoot.ProjetData.dto.player.playerStat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerStatOffensiveDto {

    private Long playerId;
    private String name;
    private String clubName;
    private Long clubId;
    private String logo;
    private Long totalBut;
    private Long totalShoot;
    private Long shootOnTarget;


    public PlayerStatOffensiveDto(Long playerId, String name, String clubName, Long clubId, String logo, Long totalBut, Long totalShoot, Long shootOnTarget) {
        this.playerId = playerId;
        this.name = name;
        this.clubName = clubName;
        this.clubId = clubId;
        this.logo=logo;
        this.totalBut = totalBut;
        this.totalShoot = totalShoot;
        this.shootOnTarget= shootOnTarget;
    }
}
