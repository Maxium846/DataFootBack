package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatMatchDto;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.model.PlayerStats;

public class PlayerStatMapper {

    public static PlayerStatMatchDto toDto(PlayerStats stats) {

        PlayerStatMatchDto dto = new PlayerStatMatchDto();

        dto.setId(stats.getId());

        dto.setPlayerId(stats.getPlayer().getId());
        dto.setMatchId(stats.getMatch().getId());
        dto.setClub(stats.getClub().getId());

        dto.setMinutePlayed(stats.getMinutePlayed());
        dto.setNote(stats.getNote());
        dto.setCaptain(stats.isCaptain());
        dto.setSubstitute(stats.isSubstitute());

        dto.setOffside(stats.getOffside());

        dto.setTotalShoot(stats.getTotalShoot());
        dto.setShootOnTarget(stats.getShootOnTarget());

        dto.setTotalGoal(stats.getTotalGoal());
        dto.setGoalConceded(stats.getGoalConceded());
        dto.setAssist(stats.getAssist());
        dto.setSaves(stats.getSaves());

        dto.setTotalPasse(stats.getTotalPasse());
        dto.setKeyPasse(stats.getKeyPasse());
        dto.setAccuracyPass(stats.getAccuracyPass());

        dto.setTotalTackle(stats.getTotalTackle());
        dto.setBlocks(stats.getBlocks());
        dto.setInterception(stats.getInterception());

        dto.setTotalDuels(stats.getTotalDuels());
        dto.setWonDuels(stats.getWonDuels());

        dto.setAttemptsDribbles(stats.getAttemptsDribbles());
        dto.setSucessDribles(stats.getSucessDribles());
        dto.setPastDribbles(stats.getPastDribbles());

        dto.setFoulsDrawns(stats.getFoulsDrawns());
        dto.setFoulsCommitted(stats.getFoulsCommitted());

        dto.setYellowCard(stats.getYellowCard());
        dto.setRedCard(stats.getRedCard());

        dto.setPenaltyWon(stats.getPenaltyWon());
        dto.setPenaltyCommited(stats.getPenaltyCommited());
        dto.setPenaltyScored(stats.getPenaltyScored());
        dto.setPenaltyMissed(stats.getPenaltyMissed());
        dto.setPenaltSaved(stats.getPenaltSaved());

        return dto;
    }
}
