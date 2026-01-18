package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.PlayerDto;
import com.dataFoot.ProjetData.model.Player;

public class PlayerMapper {


    public static PlayerDto toDto (Player player){

        PlayerDto dto = new PlayerDto();
        dto.setId(player.getId());
        dto.setAge(player.getAge());
        dto.setPosition(player.getPosition());

        return dto;
    }

    public static Player toEntity (PlayerDto dto){

        Player player = new Player();
        player.setId(dto.getId());
        player.setName(dto.name);
        player.setAge(dto.age);
        player.setPosition(dto.position);

        return player;
    }
}
