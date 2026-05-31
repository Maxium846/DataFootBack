package com.dataFoot.matchstat.dtoapi;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiFootballMatchStatStatistique {

    private String type;
    private JsonNode value;
}
