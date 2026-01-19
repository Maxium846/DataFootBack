package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.league.LeagueDetailDto;
import com.dataFoot.ProjetData.dto.league.LeagueDto;
import com.dataFoot.ProjetData.model.League;

import java.util.stream.Collectors;

public class LeagueMapper {


    public static LeagueDto toDto(League league){

        LeagueDto leagueDto = new LeagueDto();
        leagueDto.setId(league.getId());
        leagueDto.setName(league.getName());
        leagueDto.setCountry(league.getCountry());
        return leagueDto;
    }

    public static LeagueDetailDto toDetailDto( League league){

        LeagueDetailDto leagueDetailDto = new LeagueDetailDto();
        leagueDetailDto.setId(league.getId());
        leagueDetailDto.setCountry(league.getCountry());
        leagueDetailDto.setName(league.getName());
        leagueDetailDto.setClubs(
                league.getClubs().stream().map(ClubMapper::toDto).collect(Collectors.toList())
        );

        return leagueDetailDto;
    }

    public static League toEntity (LeagueDto leagueDto){
        League league = new League();
        league.setCountry(leagueDto.getCountry());
        league.setName(leagueDto.getName());


        return league;
    }
}


