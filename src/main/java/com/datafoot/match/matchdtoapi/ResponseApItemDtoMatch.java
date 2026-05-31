package com.datafoot.match.matchdtoapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseApItemDtoMatch {

    private ResponseApiMatchTeamsDto teams;

    private ResponseApiMatchGoalDto goals;
    private ResponseApiMatchFixtureDto fixture;
    private ResponseApiMatchLeagueDto league;



}
