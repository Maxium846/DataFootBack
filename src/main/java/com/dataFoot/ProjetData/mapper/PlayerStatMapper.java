package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.model.PlayerStats;

public class PlayerStatMapper {

    public static PlayerStatDto toDto(PlayerStats playerStats){

        PlayerStatDto playerStatDto = new PlayerStatDto();
        playerStatDto.setMatchesPlayed(playerStats.getMatchesPlayed());
        playerStatDto.setGoals(playerStats.getGoals());
        playerStatDto.setAssists(playerStats.getAssists());
        playerStatDto.setMinutesPlayed(playerStats.getMinutesPlayed());
        playerStatDto.setRedCard(playerStats.getRedCards());
        playerStatDto.setYellowCard(playerStats.getYellowCards());

        return playerStatDto;
    }
}
