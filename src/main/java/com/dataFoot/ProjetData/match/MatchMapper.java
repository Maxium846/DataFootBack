package com.dataFoot.ProjetData.match;

import com.dataFoot.ProjetData.match.matchdto.MatchDto;
import com.dataFoot.ProjetData.match.Match;

public class MatchMapper {

    public static MatchDto toDto(Match match) {

        return MatchDto.builder().id(match.getId()).
                played(match.isPlayed())
                .matchDate(match.getMatchDate())
                .awayTeamId(match.getAwayTeams().getId())
                .homeTeamId(match.getHomeTeams().getId())
                .journee(match.getJournee())
                .awayGoals(match.getAwayGoals())
                .homeGoals(match.getHomeGoals())
                .homeTeamName(match.getHomeTeams().getName())
                .awayTeamName(match.getAwayTeams().getName())
                .build();

    }
}
