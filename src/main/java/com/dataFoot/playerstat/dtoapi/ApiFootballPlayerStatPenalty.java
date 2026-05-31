package com.dataFoot.playerstat.dtoapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiFootballPlayerStatPenalty {
    private Integer won;
    private Integer committed;
    private Integer scored;
    private Integer missed;
    private Integer saved;
}
