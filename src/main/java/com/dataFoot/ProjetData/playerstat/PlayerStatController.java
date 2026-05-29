package com.dataFoot.ProjetData.playerstat;

import com.dataFoot.ProjetData.playerstat.playerstatdto.PlayerStatImpactDto;
import com.dataFoot.ProjetData.playerstat.playerstatdto.PlayerStatOffensiveDto;
import com.dataFoot.ProjetData.playerstat.playerstatdto.PlayerStatPasseDto;
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
