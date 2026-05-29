package com.dataFoot.ProjetData.league.dto;

import lombok.Data;

@Data
public class LeagueAffichageDto {

    private Long id;
    private String name;
    private String country;
    private int apiFootballId;
    private String logo;
    private String flag;
}
