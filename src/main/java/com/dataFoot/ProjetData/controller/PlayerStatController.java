package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.service.PlayerStatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playersStat")
public class PlayerStatController {

    private final PlayerStatService playerStatService;


    public PlayerStatController(PlayerStatService playerStatService) {
        this.playerStatService = playerStatService;
    }

    @GetMapping("/{id}")
    public PlayerStatDto getPlayerById(@PathVariable Long id){

        return  playerStatService.getStatByJoueurId(id);
    }
}
