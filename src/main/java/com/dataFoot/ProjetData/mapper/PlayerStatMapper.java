package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.model.PlayerStats;

public class PlayerStatMapper {

    public static PlayerStatDto toDto(PlayerStats playerStats){

        PlayerStatDto playerStatDto = new PlayerStatDto();
        playerStatDto.setId(playerStats.getId());
        playerStatDto.setMatchesPlayed(playerStats.getMatchesPlayed());
        playerStatDto.setGoals(playerStats.getGoals());
        playerStatDto.setAssists(playerStats.getAssists());
        playerStatDto.setMinutesPlayed(playerStats.getMinutesPlayed());
        playerStatDto.setRedCards(playerStats.getRedCards());
        playerStatDto.setYellowCards(playerStats.getYellowCards());
        playerStatDto.setJoueurId(playerStats.getPlayer().getId());

        return playerStatDto;
    }
}
