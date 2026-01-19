package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.model.Player;

public class PlayerMapper {






    public static PlayerInClubDto toInClubDto(Player player) {
        PlayerInClubDto dto = new PlayerInClubDto();
        dto.setId(player.getId());
        dto.setAge(player.getAge());
        dto.setPosition(player.getPosition());
        dto.setFirstName(player.getFirstName());
        dto.setLastName(player.getLastName());
        if (player.getClub() != null) {
            dto.setClubName(player.getClub().getName());
        }
        return dto;
    }


    public static Player toEntity (PlayerDto dto){

        Player player = new Player();
        player.setId(dto.getId());
        player.setLastName(dto.lastName);
        player.setFirstName(dto.firstName);
        player.setAge(dto.age);
        player.setPosition(dto.position);

        return player;
    }
}
