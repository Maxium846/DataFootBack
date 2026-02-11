package com.dataFoot.ProjetData.dto.match;

import lombok.Data;

@Data
public class MatchEventDto {

    private Long id;
    private Long playerId;
    private String playerName;
    private Long assistPlayerId;
    private String assistPlayerName;
    private String type;
    private int minute;
    private Long matchId;
}
