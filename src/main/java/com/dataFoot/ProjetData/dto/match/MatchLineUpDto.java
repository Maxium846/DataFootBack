package com.dataFoot.ProjetData.dto.match;

import lombok.Data;

@Data
public class MatchLineUpDto {

    private Long leagueId;
    private Long id;
    private Long playerId;
    private String playerName;
    private Long clubId;
    private String position;
    private Long matchId;
    private Boolean starter;
}
