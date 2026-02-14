package com.dataFoot.ProjetData.dto.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
@Data
public class FplFixture {
    private Long id;
    private Integer event; // numéro de journée

    @JsonProperty("team_h")
    private Integer teamHomeId;

    @JsonProperty("team_a")
    private Integer teamAwayId;

    @JsonProperty("kickoff_time")
    private OffsetDateTime kickoffTime;

    private Boolean finished;

}
