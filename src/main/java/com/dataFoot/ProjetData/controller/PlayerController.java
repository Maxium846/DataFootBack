package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.PlayersRepositoryInterface;
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

    @GetMapping("/clubs/{id}")
    public List<PlayerInClubDto> getAllPlayerByClub(@PathVariable Long id){return playerService.allPlayer(id);}


    @GetMapping("/{id}")
    public PlayerInClubDto getPlayerById(@PathVariable Long id){

        return  playerService.getPlayerById(id);
    }
    @PostMapping
    public Player createPlayer(@RequestBody PlayerDto dto) {
        return playerService.createPlayer(dto);
    }


    @DeleteMapping("/{id}")
    public void deletePlayers (@PathVariable  Long id){

         playerService.deletePlayer(id);
    }



}

