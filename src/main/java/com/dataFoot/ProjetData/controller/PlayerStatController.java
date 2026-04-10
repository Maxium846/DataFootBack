package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatImpactDto;
import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatOffensiveDto;
import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatPasseDto;
import com.dataFoot.ProjetData.service.PlayerStatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playersStat")
public class PlayerStatController {

    private final PlayerStatService playerStatService;


    public PlayerStatController(PlayerStatService playerStatService) {
        this.playerStatService = playerStatService;
    }

    @PostMapping("/match/{leagueId}")
    public void importStatMatchPlayer(@PathVariable Long leagueId) throws Exception {

         playerStatService.importStatPlayer(leagueId);
    }
    @GetMapping("/offensive/{leagueId}")
    public List<PlayerStatOffensiveDto> getStatsOffensive(
            @PathVariable Long leagueId

    ) {
        return playerStatService.getStat(leagueId);
    }

    @GetMapping("/stat/assist/{leagueId}")
    public List<PlayerStatPasseDto>getStatPasse(@PathVariable Long leagueId
    ) {

        return playerStatService.getStatPasseur(leagueId);
    }
    @GetMapping("/stats/impact/{leagueId}")
    public List<PlayerStatImpactDto> getStatInpact (@PathVariable Long leagueId){

        return playerStatService.getStatsInpact(leagueId);
    }
}
