package com.datafoot.league.mapper;

import com.datafoot.league.League;
import com.datafoot.league.dto.LeagueDto;

public class LeagueMapper {



    public static LeagueDto toDto(League league){

        return LeagueDto.builder().id(league.getId()).name(league.getName()).country(league.getCountry()).flag(league.getFlag()).logo(league.getLogo()).apiFootballId(league.getApiFootballLeagueId()).build();
    }

}


