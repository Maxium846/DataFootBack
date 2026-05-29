package com.dataFoot.team.mapper;

import com.dataFoot.team.Teams;
import com.dataFoot.team.teamdto.ListTeamDto;
import com.dataFoot.team.teamdto.TeamDto;
import com.dataFoot.league.League;
import com.dataFoot.team.teamdtoapi.ResponseApiItemsDtoTeam;

public class TeamMapper {

    public static TeamDto toDto(Teams teams) {
        TeamDto dto = new TeamDto();
        dto.setId(teams.getId());
        dto.setName(teams.getName());
        dto.setLeagueId(
                teams.getLeague() != null ? teams.getLeague().getId() : null
        );
        dto.setFondation(teams.getFounded());
        dto.setCity(teams.getCity());
        dto.setLogo(teams.getLogo());
        dto.setImage(teams.getImage());
        dto.setNameStadium(teams.getNameStadium());
        dto.setSurface(teams.getSurface());
        return dto;
    }

    public static ListTeamDto toListDto(Teams teams){

        ListTeamDto listTeamDto = new ListTeamDto();
        listTeamDto.setId(teams.getId());
        listTeamDto.setName(teams.getName());
        listTeamDto.setNameChampionnat(teams.getLeague().getName());

        return listTeamDto;
    }

    public static Teams toEntity(TeamDto dto, League league) {
        Teams teams = new Teams();
        teams.setName(dto.getName());
        teams.setLeague(league);
        return teams;
    }

   public static Teams toUpdateEntity(Teams teams, ResponseApiItemsDtoTeam dto, League league){
       teams.setApiFootballTeamId(dto.getTeam().getId());
       teams.setLogo(dto.getTeam().getLogo());
       teams.setName(dto.getTeam().getName());
       teams.setLeague(league);
       teams.setFounded(dto.getTeam().getFounded());
       teams.setImage(dto.getVenue().getImage());
       teams.setCity(dto.getVenue().getCity());
       teams.setSurface(dto.getVenue().getSurface());
       teams.setNameStadium(dto.getVenue().getName());

       return teams;
   }

}
