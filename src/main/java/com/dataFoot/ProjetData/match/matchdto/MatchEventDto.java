package com.dataFoot.ProjetData.match.matchdto;

import lombok.Data;

@Data
public class MatchEventDto {

    private Long id;
    private Long playerId;
    private Long matchId;
    private int  minutes;
    private String eventType;
    private Long teamId;
    private Long assistPlayerId;
    private String assistName;
    private Long playerOutId;
    private Long playerInId;
    private String namePlayerin;
    private String namePlayerOut;
}
