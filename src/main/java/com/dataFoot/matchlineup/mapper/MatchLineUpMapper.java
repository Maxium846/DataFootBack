package com.dataFoot.matchlineup.mapper;

import com.dataFoot.matchlineup.dto.MatchLineUpDto;
import com.dataFoot.matchlineup.MatchLineUp;

public class MatchLineUpMapper {

    public static MatchLineUpDto toDto (MatchLineUp matchLineUp){

        MatchLineUpDto matchLineUpDto = new MatchLineUpDto();
        matchLineUpDto.setId(matchLineUp.getId());
        matchLineUpDto.setStarter(matchLineUpDto.getStarter());
        matchLineUpDto.setTeamId(matchLineUp.getTeams().getId());
        matchLineUpDto.setPlayerName(matchLineUp.getPlayer().getName());
        return matchLineUpDto;
    }
}
