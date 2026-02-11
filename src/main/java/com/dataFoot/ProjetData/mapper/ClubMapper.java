package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.club.ClubDetailDto;
import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;

import java.util.stream.Collectors;

public class ClubMapper {

    public static ClubDto toDto(Club club) {
        ClubDto dto = new ClubDto();
        dto.setId(club.getId());
        dto.setName(club.getName());
        dto.setLeagueId(
                club.getLeague() != null ? club.getLeague().getId() : null
        );
        dto.setEntraineur(club.getEntraineur());
        dto.setPresident(club.getPresident());
        dto.setDateCreation(club.getDateCreation());
        return dto;
    }

    public static Club toEntity(ClubDto dto, League league) {
        Club club = new Club();
        club.setName(dto.getName());
        club.setLeague(league);
        return club;
    }
    public static void updateEntity(Club club, ClubDto dto) {
        club.setName(dto.name);
    }
}
