package com.datafoot.player;

import com.datafoot.player.dto.PlayerApiDto;
import com.datafoot.player.dto.PlayerDto;
import com.datafoot.player.dto.PlayerInClubDto;
import com.datafoot.playerstat.playerstatdto.PlayerStatDto;
import org.springframework.http.HttpStatus;
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
    public List<PlayerDto> getPlayerByGuessEasy(){

        return playerService.getPlayerByClubByClassement();
    }
    @GetMapping("/difficulte/{difficulty}")
    public ResponseEntity<List<PlayerDto>> getListPlayerForFilter(@PathVariable String difficulty){
        List<PlayerDto> players = playerService.getPlayerByDifficultyOpti(difficulty);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public PlayerInClubDto getPlayerById(@PathVariable Long id){

        return  playerService.getPlayerById(id);
    }

    @PostMapping("/import/{leagueId}")
    public ResponseEntity<List<PlayerApiDto>> importPlayerFromApi(@PathVariable Long leagueId) throws Exception {
         List<PlayerApiDto> dto = playerImportService.importOrUpdatePlayers(leagueId);

         return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{playerId}/stat")
    public ResponseEntity<PlayerStatDto> getStatByPlayer(@PathVariable int playerId) {
        PlayerStatDto events = playerService.getPlayerStats(playerId);
        return ResponseEntity.ok(events);
    }


    @GetMapping("/allPlayers")
    public  List<PlayerDto> getAllPlayer(){

        return playerService.allPlayerInAllLeague();
    }

}

