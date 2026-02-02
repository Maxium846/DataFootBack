package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.model.Classement;

public class ClassementMapper {






    public static ClassementDto toDto(Classement c) {
        ClassementDto dto = new ClassementDto();
        dto.setClubId(c.getClub().getId());
        dto.setClubName(c.getClub().getName());
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

}


