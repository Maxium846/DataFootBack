package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.league.LeagueAffichageDto;
import com.dataFoot.ProjetData.dto.league.LeagueByIdDto;
import com.dataFoot.ProjetData.dto.league.LeagueDto;
import com.dataFoot.ProjetData.model.League;

import java.util.stream.Collectors;

public class LeagueMapper {


    public static LeagueAffichageDto toDtoAfichage(League league){

        LeagueAffichageDto leagueAffichageDto = new LeagueAffichageDto();
        leagueAffichageDto.setId(league.getId());
        leagueAffichageDto.setName(league.getName());
        return leagueAffichageDto;
    }
    public static LeagueDto toDto(League league){

        LeagueDto leagueDto = new LeagueDto();
        leagueDto.setId(league.getId());
        leagueDto.setName(league.getName());
        return leagueDto;
    }
    public static LeagueByIdDto toDetailDto(League league){

        LeagueByIdDto leagueDetailDto = new LeagueByIdDto();
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


