package com.dataFoot.matchevent.dtoapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiFootballEventItem {

    private ApiFootballEventTime time;
    private ApiFootballEventTeam team;
    private ApiFootballEventPlayer player;
    private ApiFootballEventAssist assist;
    private String type;
    private String detail;
    private String comments;

}
