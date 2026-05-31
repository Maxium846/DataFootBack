package com.dataFoot.matchstat.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiFootballMatchStatItems {

    private  ApiFootballMatchStatTeam team;

    private  List<ApiFootballMatchStatStatistique> statistics;

}
