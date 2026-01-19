package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {


    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerInClubDto> getAllPlayer(PlayerInClubDto dto){return playerService.allPlayer(dto);}
    @PostMapping
    public Player createPlayer(@RequestBody PlayerDto dto) {
        return playerService.createPlayer(dto);
    }

}

