package com.dataFoot.playerstat.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiFootballPlayerStatPlayers {

    private ApiFootballPlayerStatPlayer player;
    private List<ApiFootballPlayerStatStatistique> statistics;

}
