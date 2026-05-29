package com.dataFoot.ranking;

import com.dataFoot.ranking.rankingdto.RankingDto;
import com.dataFoot.ranking.rankingdto.RankinkDtoAccueil;

public class RankingMapper {






    public static RankingDto toDto(Ranking c) {
        RankingDto dto = new RankingDto();
        dto.setTeamId(c.getTeams().getId());
        dto.setTeamName(c.getTeams().getName());
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

    public static RankinkDtoAccueil toDtoPageAccueil(Ranking c) {
        RankinkDtoAccueil dto = new RankinkDtoAccueil();
        dto.setClubId(c.getTeams().getId());
        dto.setClubName(c.getTeams().getName());
        dto.setPlayed(c.getPlayed());
        dto.setLeagueName(c.getLeague().getName());
        dto.setPoints(c.getPoints());

        return dto;
    }

}


