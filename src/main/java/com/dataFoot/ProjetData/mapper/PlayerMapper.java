package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.model.Player;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PlayerMapper {


    public static PlayerInClubDto toInClubDto(Player player) {
        PlayerInClubDto dto = new PlayerInClubDto();
        dto.setId(player.getId());

        if (player.getDateDeNaissance() != null){
            dto.setDateDeNaissance(player.getDateDeNaissance());
        }
        if (player.getPosition() != null) {

            dto.setPosition(player.getPosition().name());

        }
        dto.setFirstName(player.getFirstName());
        dto.setLastName(player.getLastName());
        dto.setAge(player.getDateDeNaissance() != null
                ? (int) player.getDateDeNaissance().until(LocalDate.now(), ChronoUnit.YEARS)
                : 0); // ou null si ton champ peut être Integer
        if (player.getClub() != null) {
            dto.setClubName(player.getClub().getName());
        }
        dto.setNation(player.getNation());
        dto.setPhoto(player.getPhoto());
        dto.setLeagueName(player.getClub().getLeague().getName());
        return dto;
    }

    public static PlayerDto toDto(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.setId(player.getId());
        if(player.getClub() != null){
            dto.setClubId(player.getClub().getId());

        }

        if (player.getDateDeNaissance() != null){
            dto.setDateDeNaissance(player.getDateDeNaissance());
        }
        if (player.getPosition() != null) {

            dto.setPosition(player.getPosition().name());

        }
        dto.setFirstName(player.getFirstName());
        dto.setAge(player.getDateDeNaissance() != null
                ? (int) player.getDateDeNaissance().until(LocalDate.now(), ChronoUnit.YEARS)
                : 0); // ou null si ton champ peut être Integer
        if (player.getClub() != null) {
            dto.setClubName(player.getClub().getName());
        }
        dto.setNation(player.getNation());
        dto.setPhoto(player.getPhoto());
        if(player.getClub() != null){
            dto.setLeagueName(player.getClub().getLeague().getName());
        }
        if(player.getClub() != null) {

            dto.setLogo(player.getClub().getLogo());
        }
        return dto;
    }


    public static Player toEntity (PlayerDto dto){

        Player player = new Player();
        player.setId(dto.getId());
        player.setFirstName(dto.getFirstName());
        player.setDateDeNaissance(dto.getDateDeNaissance());
        return player;
    }
}
