package com.dataFoot.match.matchdtoapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseApiMatchFixtureStatus {

    @JsonProperty("short")
    private String shortStatus;

    @JsonProperty("long")
    private String longStatus;}
