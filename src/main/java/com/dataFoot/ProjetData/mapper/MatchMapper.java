package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.match.MatchDto;
import com.dataFoot.ProjetData.model.Match;

public class MatchMapper {

    public static MatchDto toDto(Match match) {
        MatchDto dto = new MatchDto();
        dto.setId(match.getId());
        dto.setMatchDate(match.getMatchDate());

        dto.setHomeClubId(match.getHomeClub().getId());
        dto.setHomeClubName(match.getHomeClub().getName());

        dto.setAwayClubId(match.getAwayClub().getId());
        dto.setAwayClubName(match.getAwayClub().getName());

        dto.setHomeGoals(match.getHomeGoals());
        dto.setAwayGoals(match.getAwayGoals());
        dto.setPlayed(match.isPlayed());
        dto.setJournee(match.getJournee());


        return dto;
    }
}
