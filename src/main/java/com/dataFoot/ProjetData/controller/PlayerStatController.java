package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatButeurDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatMatchDto;
import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatPasseurDto;
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

    @GetMapping("/{id}")
    public PlayerStatMatchDto getPlayerById(@PathVariable Long id){

        return  playerStatService.getStatByJoueurId(id);
    }

    @PostMapping("/match/{leagueId}")
    public void importStatMatchPlayer(@PathVariable Long leagueId) throws Exception {

         playerStatService.importStatPlayer(leagueId);
    }
    @GetMapping("/stat/{leagueId}")
    public ResponseEntity<List<PlayerStatButeurDto>> getStat(@PathVariable Long leagueId) {
        List<PlayerStatButeurDto> playerStatButeurDto =playerStatService.getStat(leagueId);
        return ResponseEntity.ok(playerStatButeurDto);
    }

    @GetMapping("/stat/assist/{leagueId}")
    public ResponseEntity<List<PlayerStatPasseurDto>> getStatPasse(@PathVariable Long leagueId) {


        List<PlayerStatPasseurDto> playerStatPasseur = playerStatService.getStatPasseur(leagueId);
        return ResponseEntity.ok(playerStatPasseur);
    }
}
