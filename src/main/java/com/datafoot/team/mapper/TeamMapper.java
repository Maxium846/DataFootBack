package com.datafoot.team.mapper;

import com.datafoot.team.Team;
import com.datafoot.team.teamdto.ListTeamDto;
import com.datafoot.team.teamdto.TeamDto;

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
