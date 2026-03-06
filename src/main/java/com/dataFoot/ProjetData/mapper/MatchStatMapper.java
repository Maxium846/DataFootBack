package com.dataFoot.ProjetData.mapper;

import com.dataFoot.ProjetData.dto.match.MatchstatDto;
import com.dataFoot.ProjetData.model.MatchStat;

public class MatchStatMapper {


    public static MatchstatDto toInDto(MatchStat matchStat){

        MatchstatDto matchstatDto = new MatchstatDto();

        matchstatDto.setId(matchStat.getId());
        matchstatDto.setBlockedShots(matchStat.getBlockedShots());
        matchstatDto.setClubId(matchStat.getClubId().getId());
        matchstatDto.setMatch(matchStat.getMatch().getId());
        matchstatDto.setFouls(matchStat.getFouls());
        matchstatDto.setBallPossession(matchStat.getBallPossession());
        matchstatDto.setCornerKick(matchStat.getCornerKick());
        matchstatDto.setYellowCards(matchStat.getYellowCards());
        matchstatDto.setRedCards(matchStat.getRedCards());
        matchstatDto.setExpectedGoals(matchStat.getExpectedGoals());
        matchstatDto.setOffsides(matchStat.getOffsides());
        matchstatDto.setShotInsideBox(matchStat.getShotInsideBox());
        matchstatDto.setShootsOnGoals(matchStat.getShootsOnGoals());
        matchstatDto.setShootOffGoals(matchStat.getShootOffGoals());
        matchstatDto.setShotsOutsideBox(matchStat.getShotsOutsideBox());
        matchstatDto.setRedCards(matchStat.getRedCards());
        matchstatDto.setTotalPasses(matchStat.getTotalPasses());
        matchstatDto.setPassesAccurate(matchStat.getPassesAccurate());
        matchstatDto.setPassesPercentage(matchStat.getPassesPercentage());
        matchstatDto.setGoalsPrevented(matchStat.getGoalsPrevented());
        matchstatDto.setTotalShots(matchStat.getTotalShots());
        matchstatDto.setGoalkeeperSave(matchStat.getGoalkeeperSave());
        matchstatDto.setNameClub(matchStat.getClubId().getName());

        return matchstatDto;
    }
}
