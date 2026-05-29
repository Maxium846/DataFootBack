package com.dataFoot.match;

import com.dataFoot.match.matchdto.MatchDto;

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
