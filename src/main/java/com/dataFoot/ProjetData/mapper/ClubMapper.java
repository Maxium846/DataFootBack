package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.club.ClubDetailDto;
import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.model.Club;

import java.util.stream.Collectors;

public class ClubMapper {

    public static ClubDto toDto(Club club) {
        ClubDto dto = new ClubDto();
        dto.setId(club.getId());
        dto.setName(club.getName());
        dto.setCountry(club.getCountry());
        dto.setLeagueId(club.getLeague().getName());
        return dto;

    }

    // Pour le détail complet avec les joueurs
    public static ClubDetailDto toDetailDto(Club club) {
        ClubDetailDto dto = new ClubDetailDto();
        dto.setId(club.getId());
        dto.setName(club.getName());
        dto.setCountry(club.getCountry());
        if (club.getLeague() != null) {
            dto.setLeague(club.getLeague().getName());
        }
        dto.setPlayer(
                club.getPlayer().stream()
                        .map(PlayerMapper::toInClubDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }
    public static Club toEntity(ClubDto dto) {
        Club club = new Club();
        club.setName(dto.name);
        club.setCountry(dto.country);
        return club;
    }
    public static void updateEntity(Club club, ClubDto dto) {
        club.setName(dto.name);
        club.setCountry(dto.country);
    }
}
