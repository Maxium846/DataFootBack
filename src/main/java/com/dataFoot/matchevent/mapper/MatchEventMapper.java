package com.dataFoot.matchevent.mapper;

import com.dataFoot.match.matchdto.MatchEventDto;
import com.dataFoot.matchevent.MatchEvent;

public class MatchEventMapper {

    public static MatchEventDto toDto(MatchEvent event) {

        MatchEventDto dto = new MatchEventDto();

        dto.setId(event.getId());
        dto.setMatchId(event.getMatch().getId());
        dto.setMinutes(event.getMinute());
        dto.setTeamId(event.getTeam().getId());

        if (event.getEventType() != null) {
            dto.setEventType(event.getEventType().name());
        }

        if (event.getPlayer() != null) {
            dto.setPlayerId(event.getPlayer().getId());
        }

        if (event.getAssistPlayer() != null) {
            dto.setAssistPlayerId(event.getAssistPlayer().getId());
        }
        if (event.getAssistPlayer() != null) {
            dto.setAssistName(event.getAssistName());
        }

        if (event.getPlayerOut() != null) {
            dto.setPlayerOutId(event.getPlayerOut().getId());
            dto.setNamePlayerOut(event.getPlayerOutName());
        }

        if (event.getPlayerIn() != null) {
            dto.setPlayerInId(event.getPlayerIn().getId());
            dto.setNamePlayerin(event.getPlayerInName());
        }


        return dto;
    }
}
