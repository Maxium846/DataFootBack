package com.dataFoot.playerstat.dtoapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiFootballPlayerStatGoals {
    private Integer total;
    private Integer conceded;
    private Integer assists;
    private String accuracy;
    private Integer save;
}
