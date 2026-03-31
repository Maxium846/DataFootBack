package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatOffensiveDto;
import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatPasseDto;
import com.dataFoot.ProjetData.service.PlayerStatService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    public Page<PlayerStatOffensiveDto> getStatsOffensive(
            @PathVariable Long leagueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return playerStatService.getStat(leagueId, page, size);
    }

    @GetMapping("/stat/assist/{leagueId}")
    public ResponseEntity<Page<PlayerStatPasseDto>> getStatPasse(@PathVariable Long leagueId,
    @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        Page<PlayerStatPasseDto> playerStatPasseur = playerStatService.getStatPasseur(leagueId,page,size);
        return ResponseEntity.ok(playerStatPasseur);
    }
}
