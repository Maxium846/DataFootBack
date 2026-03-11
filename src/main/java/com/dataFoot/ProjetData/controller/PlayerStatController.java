package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.match.MatchEventDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatMatchDto;
import com.dataFoot.ProjetData.service.PlayerStatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playersStat")
public class PlayerStatController {

    private final PlayerStatService playerStatService;


    public PlayerStatController(PlayerStatService playerStatService) {
        this.playerStatService = playerStatService;
    }

    @GetMapping("/{id}")
    public PlayerStatMatchDto getPlayerById(@PathVariable Long id){

        return  playerStatService.getStatByJoueurId(id);
    }

    @PostMapping("/match/{leagueId}")
    public void importStatMatchPlayer(@PathVariable Long leagueId) throws Exception {

         playerStatService.importStatPlayer(leagueId);
    }

}
