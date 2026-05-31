package com.datafoot.matchlineup;

import com.datafoot.matchlineup.dto.MatchLineUpDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchLineUpController {

    private final MatchLineUpService lineupService;

    public MatchLineUpController(MatchLineUpService lineupService) {
        this.lineupService = lineupService;
    }

    // 🔹 Récupérer la composition d'un match
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchLineUpDto>> getLineUpByMatch(@PathVariable Long matchId) {
        List<MatchLineUpDto> lineups = lineupService.getLineUpByMatch(matchId);
        return ResponseEntity.ok(lineups);
    }


    @PostMapping("matchLineUp/{leagueId}")
    public String importLineUp( @PathVariable int leagueId){

        return lineupService.importLineUpForLeague(leagueId);

    }

}
