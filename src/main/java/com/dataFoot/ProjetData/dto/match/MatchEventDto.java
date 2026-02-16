package com.dataFoot.ProjetData.dto.match;

import lombok.Data;

@Data
public class MatchEventDto {

    private Long id;
    private Long playerId;
    private Long matchId;
    private int  minutes;
    private String eventType;
    private Long clubId;
}
