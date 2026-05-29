package com.dataFoot.league.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeagueDto {

    private Long id;
    private String name;
    private String country;
    private Integer apiFootballId;
    private String logo;
    private String flag;

}
