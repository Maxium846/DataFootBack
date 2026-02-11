package com.dataFoot.ProjetData.dto.match;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MatchDto {

    private Long id;
    private LocalDate matchDate;

    private Long homeClubId;
    private String homeClubName;

    private Long awayClubId;
    private String awayClubName;

    private Integer homeGoals;
    private Integer awayGoals;

    private boolean played;
    private Integer journee;
}
