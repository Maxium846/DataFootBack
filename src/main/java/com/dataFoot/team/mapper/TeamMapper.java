package com.dataFoot.team.mapper;

import com.dataFoot.team.Team;
import com.dataFoot.team.teamdto.ListTeamDto;
import com.dataFoot.team.teamdto.TeamDto;
import com.dataFoot.league.League;
import com.dataFoot.team.teamdtoapi.ResponseApiItemsDtoTeam;

public class TeamMapper {

    public static TeamDto toDto(Team team) {
        TeamDto dto = new TeamDto();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setLeagueId(
                team.getLeague() != null ? team.getLeague().getId() : null
        );
        dto.setFondation(team.getFounded());
        dto.setCity(team.getCity());
        dto.setLogo(team.getLogo());
        dto.setImage(team.getImage());
        dto.setNameStadium(team.getNameStadium());
        dto.setSurface(team.getSurface());
        return dto;
    }

    public static ListTeamDto toListDto(Team team){

        ListTeamDto listTeamDto = new ListTeamDto();
        listTeamDto.setId(team.getId());
        listTeamDto.setName(team.getName());
        listTeamDto.setNameChampionnat(team.getLeague().getName());

        return listTeamDto;
    }


}
