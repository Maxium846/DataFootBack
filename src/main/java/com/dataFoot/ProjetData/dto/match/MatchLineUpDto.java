package com.dataFoot.ProjetData.dto.match;

import com.dataFoot.ProjetData.enumeration.Position;
import lombok.Data;

@Data
public class MatchLineUpDto {

    private Long leagueId;
    private Long id;
    private Long playerId;
    private String playerName;
    private Long clubId;
    private Position position;
    private Long matchId;
    private Boolean starter;
    private String note;
}
