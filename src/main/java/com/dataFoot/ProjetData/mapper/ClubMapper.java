package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.ClubDto;
import com.dataFoot.ProjetData.model.Club;

public class ClubMapper {

    public static ClubDto toDto (Club club){


        ClubDto dto = new ClubDto();
        dto.id = club.getId();
        dto.name = club.getName();;
        dto.league = club.getLeague();
        dto.country = club.getCountry();

        return dto;

    }
    public static Club toEntity(ClubDto dto) {
        Club club = new Club();
        club.setName(dto.name);
        club.setLeague(dto.league);
        club.setCountry(dto.country);
        return club;
    }
    public static void updateEntity(Club club, ClubDto dto) {
        club.setName(dto.name);
        club.setLeague(dto.league);
        club.setCountry(dto.country);
    }
}
