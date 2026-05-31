package com.datafoot.matchlineup.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiFootballLineupItem {

    private ApiFootballLineupTeam team;

    private List<ApiFootballLineupStarter> startXI ;

    private List<ApiFootballLineupSubstitute> substitutes;

}
