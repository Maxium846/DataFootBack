package com.dataFoot.playerstat.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiFootballPlayerStatStatistique {


    private ApiFootballPlayerStatGames games;
    private ApiFootballPlayerStatShots shots;

    private Integer offside;
    private ApiFootballPlayerStatGoals goals;

    private ApiFootballPlayerStatPasses passes;

    private ApiFootballPlayerStatTackle tackles;
    private ApiFootballPlayerStatDuels duels;

    private ApiFootballPlayerStatDribbles dribbles;
    private ApiFootballPlayerStatFouls fouls;
    private ApiFootballPlayerStatCards cards;

    private ApiFootballPlayerStatPenalty penalty;

}
