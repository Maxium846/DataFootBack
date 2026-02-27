package com.dataFoot.ProjetData.dto.player;

import lombok.Data;

@Data
public class PlayerStatDto {
    private int matchesPlayed;
    private int goals;
    private int assists;
    private int yellowCard;
    private int redCard;
    private int minutesPlayed;
    private int playerId;
    private int ownGoals;
    private int penaltyMissed;
    private int penalty;
    private long totalEvents;
}
