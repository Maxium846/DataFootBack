package com.dataFoot.matchlineup.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiFootballLineupItem {

    private ApiFootballLinupTeam team;

    private List<ApiFootballLineupStarter> startXI ;

    private List<ApiFootballLineupSubstitute> substitutes;

}
