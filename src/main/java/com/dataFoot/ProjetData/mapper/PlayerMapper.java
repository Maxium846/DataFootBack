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
        dto.setPosition(player.getPosition().name());
        dto.setFirstName(player.getFirstName());
        dto.setLastName(player.getLastName());
        dto.setAge(player.getDateDeNaissance() != null
                ? (int) player.getDateDeNaissance().until(LocalDate.now(), ChronoUnit.YEARS)
                : 0); // ou null si ton champ peut être Integer
        if (player.getClub() != null) {
            dto.setClubName(player.getClub().getName());
        }
        dto.setNation(player.getNation());
        return dto;
    }


    public static Player toEntity (PlayerDto dto){

        Player player = new Player();
        player.setId(dto.getId());
        player.setLastName(dto.getLastName());
        player.setFirstName(dto.getFirstName());
        player.setDateDeNaissance(dto.getDateDeNaissance());
        player.setPosition(dto.getPosition());

        return player;
    }
}
