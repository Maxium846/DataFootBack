package com.dataFoot.league.mapper;

import com.dataFoot.league.League;
import com.dataFoot.league.dto.LeagueDto;

public class LeagueMapper {



    public static LeagueDto toDto(League league){

        return LeagueDto.builder().id(league.getId()).name(league.getName()).country(league.getCountry()).flag(league.getFlag()).logo(league.getLogo()).apiFootballId(league.getApiFootballLeagueId()).build();
    }

}


