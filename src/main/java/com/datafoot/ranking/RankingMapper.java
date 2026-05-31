package com.datafoot.ranking;

import com.datafoot.ranking.rankingdto.RankingDto;
import com.datafoot.ranking.rankingdto.RankingDtoAccueil;

public class RankingMapper {






    public static RankingDto toDto(Ranking c) {
        RankingDto dto = new RankingDto();
        dto.setTeamId(c.getTeam().getId());
        dto.setTeamName(c.getTeam().getName());
        dto.setPlayed(c.getPlayed());
        dto.setWins(c.getWins());
        dto.setDraws(c.getDraws());
        dto.setLosses(c.getLosses());
        dto.setGoalsFor(c.getGoalsFor());
        dto.setGoalsAgainst(c.getGoalsAgainst());
        dto.setPoints(c.getPoints());
        dto.setGoalDifference(c.getGoalDifference());
        return dto;
    }

    public static RankingDtoAccueil toDtoPageAccueil(Ranking c) {
        RankingDtoAccueil dto = new RankingDtoAccueil();
        dto.setClubId(c.getTeam().getId());
        dto.setClubName(c.getTeam().getName());
        dto.setPlayed(c.getPlayed());
        dto.setLeagueName(c.getLeague().getName());
        dto.setPoints(c.getPoints());

        return dto;
    }

}


