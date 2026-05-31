package com.datafoot.matchlineup.mapper;

import com.datafoot.matchlineup.dto.MatchLineUpDto;
import com.datafoot.matchlineup.MatchLineUp;

public class MatchLineUpMapper {

    public static MatchLineUpDto toDto(MatchLineUp lineup) {
        MatchLineUpDto dto = new MatchLineUpDto();

        dto.setId(lineup.getId());
        dto.setPlayerId(lineup.getPlayers().getId());
        dto.setPlayerName(lineup.getPlayers().getName());
        dto.setTeamId(lineup.getTeam().getId());
        dto.setPosition(lineup.getPosition());
        dto.setMatchId(lineup.getMatch().getId());
        dto.setStarter(lineup.isStarter());

        return dto;

    }
}
