package com.dataFoot.ProjetData.dto.player.playerStat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerStatPasseurDto {

    private Long playerId;
    private String name;
    private String clubName;
    private Long clubId;
    private String logo;
    private Long assist;


    public PlayerStatPasseurDto(Long playerId, String name, String clubName, Long clubId,String logo,Long assist) {
        this.playerId = playerId;
        this.name = name;
        this.clubName = clubName;
        this.clubId = clubId;
        this.logo=logo;
        this.assist= assist;

    }


}
