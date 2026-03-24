package com.dataFoot.ProjetData.dto.player.playerStat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerStatPasseDto {

    private Long playerId;
    private String name;
    private String clubName;
    private Long clubId;
    private String logo;
    private Long assist;
    private Long keyPasse;
    private Long totalPasse;
    private Long accuracyPass;




    public PlayerStatPasseDto(Long playerId, String name, String clubName, Long clubId, String logo, Long assist,Long keyPasse,Long totalPasse,Long accuracyPass) {
        this.playerId = playerId;
        this.name = name;
        this.clubName = clubName;
        this.clubId = clubId;
        this.logo=logo;
        this.assist= assist;
        this.keyPasse=keyPasse;
        this.totalPasse=totalPasse;
        this.accuracyPass= accuracyPass;

    }


}
