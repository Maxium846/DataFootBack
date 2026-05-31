package com.datafoot.matchlineup.dto;

import com.datafoot.enumeration.Position;
import lombok.Data;

@Data
public class MatchLineUpDto {

    private Long leagueId;
    private Long id;
    private Long playerId;
    private String playerName;
    private Long teamId;
    private Position position;
    private Long matchId;
    private Boolean starter;
    private String note;
}
