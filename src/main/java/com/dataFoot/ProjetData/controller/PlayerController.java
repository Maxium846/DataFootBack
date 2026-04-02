package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.service.PlayerImportService;
import com.dataFoot.ProjetData.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {


    private final PlayerService playerService;
    private final PlayerImportService playerImportService;

    public PlayerController(PlayerService playerService, PlayerImportService playerImportService) {
        this.playerService = playerService;
        this.playerImportService = playerImportService;
    }

    @GetMapping("/clubs/{id}")
    public List<PlayerInClubDto> getAllPlayerByClub(@PathVariable Long id){return playerService.allPlayer(id);}

    @GetMapping("/guessFacile")
    public List<PlayerDto> getPlayerByGuessFacile(){

        return playerService.getPlayerByClubByClassement();
    }

    @GetMapping("/difficulte/{difficulty}")
    public List<PlayerDto> getPlayerByDifficulty(@PathVariable String difficulty){

        return playerService.getPlayerByDifficultyOpti(difficulty);
    }

    @GetMapping("/{id}")
    public PlayerInClubDto getPlayerById(@PathVariable Long id){

        return  playerService.getPlayerById(id);
    }

    @PostMapping("/generate-players/{leagueId}")
    public ResponseEntity<Void> generatePlayers(@PathVariable Long leagueId) throws Exception {
        return ResponseEntity.ok(playerImportService.generateOrUpdatePlayers(leagueId));
    }

    @GetMapping("/{playerId}/stat")
    public ResponseEntity<PlayerStatDto> getStatByPlayer(@PathVariable int playerId) {
        PlayerStatDto events = playerService.getPlayerStats(playerId);
        return ResponseEntity.ok(events);
    }


    @GetMapping("/allPlayers")
    public  List<PlayerDto> getAllJoueur(){

        return playerService.allPlayerInAllLeague();
    }





}

