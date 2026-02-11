package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.match.MatchLineUpDto;
import com.dataFoot.ProjetData.model.MatchLineUp;

public class MatchLineUpMapper {

    public static MatchLineUpDto toDto (MatchLineUp matchLineUp){

        MatchLineUpDto matchLineUpDto = new MatchLineUpDto();
        matchLineUpDto.setId(matchLineUp.getId());
        matchLineUpDto.setStarter(matchLineUpDto.getStarter());
        matchLineUpDto.setClubId(matchLineUp.getClub().getId());
        matchLineUpDto.setPlayerName(matchLineUp.getPlayer().getFirstName() + " " + matchLineUp.getPlayer().getLastName()) ;
        matchLineUpDto.setPosition(matchLineUp.getPosition());

        return matchLineUpDto;
    }
}
