package com.dataFoot.ProjetData.dto.player.playerStat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerStatButeurDto {

    private Long playerId;
    private String name;
    private String clubName;
    private Long clubId;
    private Long totalBut;


    public PlayerStatButeurDto(Long playerId, String name, String clubName, Long clubId,Long totalBut) {
        this.playerId = playerId;
        this.name = name;
        this.clubName = clubName;
        this.clubId = clubId;
        this.totalBut = totalBut;
    }
}
