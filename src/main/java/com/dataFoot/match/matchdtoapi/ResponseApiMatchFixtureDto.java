package com.dataFoot.match.matchdtoapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseApiMatchFixtureDto {
    private  int id;
    private String date;
    private ResponseApiMatchFixtureStatus status;
}
