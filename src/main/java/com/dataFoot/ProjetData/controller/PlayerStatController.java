package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatOffensiveDto;
import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatPasseDto;
import com.dataFoot.ProjetData.service.PlayerStatService;
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
    @GetMapping("/stat/{leagueId}")
    public ResponseEntity<List<PlayerStatOffensiveDto>> getStat(@PathVariable Long leagueId) {
        List<PlayerStatOffensiveDto> playerStatButeurDto =playerStatService.getStat(leagueId);
        return ResponseEntity.ok(playerStatButeurDto);
    }

    @GetMapping("/stat/assist/{leagueId}")
    public ResponseEntity<List<PlayerStatPasseDto>> getStatPasse(@PathVariable Long leagueId) {


        List<PlayerStatPasseDto> playerStatPasseur = playerStatService.getStatPasseur(leagueId);
        return ResponseEntity.ok(playerStatPasseur);
    }
}
