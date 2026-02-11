package com.dataFoot.ProjetData.dto.player;

import lombok.Data;

@Data
public class PlayerStatDto {
    private Long id;
    private int matchesPlayed;
    private int goals;
    private int assists;
    private int yellowCards;
    private int redCards;
    private int minutesPlayed;
    private Long joueurId;
}
