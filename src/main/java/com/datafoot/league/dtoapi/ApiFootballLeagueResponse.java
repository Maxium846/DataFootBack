package com.datafoot.league.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiFootballLeagueResponse {

    private List<ApiFootballLeagueItems> response;
}
