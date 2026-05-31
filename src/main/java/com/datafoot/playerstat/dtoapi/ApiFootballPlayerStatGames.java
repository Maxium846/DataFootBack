package com.datafoot.playerstat.dtoapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiFootballPlayerStatGames {

    private Integer minutes;
    private Integer number;
    private String position;
    private String rating;
    private boolean captain;
    private boolean substitute;
}
