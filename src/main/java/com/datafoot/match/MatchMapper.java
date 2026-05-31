package com.datafoot.match;

import com.datafoot.match.matchdto.MatchDto;

public class MatchMapper {

    public static MatchDto toDto(Match match) {

        return MatchDto.builder().id(match.getId()).
                played(match.isPlayed())
                .matchDate(match.getMatchDate())
                .awayTeamId(match.getAwayTeam().getId())
                .homeTeamId(match.getHomeTeam().getId())
                .journee(match.getJournee())
                .awayGoals(match.getAwayGoals())
                .homeGoals(match.getHomeGoals())
                .homeTeamName(match.getHomeTeam().getName())
                .awayTeamName(match.getAwayTeam().getName())
                .build();

    }
}
