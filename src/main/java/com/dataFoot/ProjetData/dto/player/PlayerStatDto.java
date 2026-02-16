package com.dataFoot.ProjetData.dto.player;

import lombok.Data;

@Data
public class PlayerStatDto {
    private int matchesPlayed;
    private Long goals;
    private Long assists;
    private Long yellowCard;
    private Long redCard;
    private Long minutesPlayed;
    private Long playerId;
    private long totalEvents;
}
