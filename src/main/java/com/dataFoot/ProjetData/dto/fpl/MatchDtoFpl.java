package com.dataFoot.ProjetData.dto.fpl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
@Data
public class MatchDtoFpl {


    @JsonProperty("id")
    private Long id;
    @JsonProperty("event")
    private Integer event; // numéro de journée

    @JsonProperty("team_h")
    private Integer teamHomeId;

    @JsonProperty("team_a")
    private Integer teamAwayId;

    @JsonProperty("kickoff_time")
    private OffsetDateTime kickoffTime;

    @JsonProperty("finished")
    private Boolean finished;
    @JsonProperty("team_a_score")
    private Integer teamAwayScore;
    @JsonProperty("team_h_score")
    private Integer teamHomeScore;


}
