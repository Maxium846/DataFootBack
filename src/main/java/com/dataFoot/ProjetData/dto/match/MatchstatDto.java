package com.dataFoot.ProjetData.dto.match;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchstatDto {

    private Long id;
    private Integer shootsOnGoals;
    private Integer shootOffGoals;
    private Integer totalShots;
    private Integer blockedShots;
    private Integer shotInsideBox;
    private Integer shotsOutsideBox;
    private Integer fouls;
    private Integer cornerKick;
    private Integer offsides;
    private Integer ballPossession;
    private Integer yellowCards;
    private Integer redCards;
    private Integer goalkeeperSave;
    private Integer totalPasses;
    private Integer passesAccurate;
    private Integer passesPercentage;
    private Integer expectedGoals;
    private Integer goalsPrevented;

    private Long match;

    private Long clubId;
    private String NameClub;
}
