package com.dataFoot.ProjetData.dto.player;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PlayerStatClassementDto {

    private Long playerId;
    private Integer totalBut;
    private String name;
    private String clubName;
}
