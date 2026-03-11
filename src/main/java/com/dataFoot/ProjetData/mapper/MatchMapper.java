package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.match.MatchDto;
import com.dataFoot.ProjetData.model.Match;

public class MatchMapper {

    public static MatchDto toDto(Match match) {

        return MatchDto.builder().id(match.getId()).
                played(match.isPlayed())
                .matchDate(match.getMatchDate())
                .awayClubId(match.getAwayClub().getId())
                .homeClubId(match.getHomeClub().getId())
                .journee(match.getJournee())
                .awayGoals(match.getAwayGoals())
                .homeGoals(match.getHomeGoals())
                .homeClubName(match.getHomeClub().getName())
                .awayClubName(match.getAwayClub().getName())
                .build();

    }
}
