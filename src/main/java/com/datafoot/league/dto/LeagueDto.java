package com.datafoot.league.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeagueDto {

    private Long id;
    private String name;
    private String country;
    private Integer apiFootballId;
    private String logo;
    private String flag;

}
