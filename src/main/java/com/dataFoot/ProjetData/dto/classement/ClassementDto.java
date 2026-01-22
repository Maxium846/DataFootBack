package com.dataFoot.ProjetData.dto.classement;

import lombok.Data;

@Data
public class ClassementDto {
    private Long clubId;
    private String clubName;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;
    private int goalDifference;
}
