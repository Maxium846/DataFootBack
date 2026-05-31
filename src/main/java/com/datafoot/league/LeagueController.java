package com.datafoot.league;

import com.datafoot.league.dto.LeagueDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
public class LeagueController {
    private final LeagueService leagueService;
    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }
    @GetMapping
    public ResponseEntity<List<LeagueDto>> getAllLeagues() {

        return ResponseEntity.ok(leagueService.findAll());
    }

    @PostMapping("/import/{apiFootballId}")
    public ResponseEntity<LeagueDto> importLeagueByApiFootball(@PathVariable int apiFootballId){

        LeagueDto league = leagueService.importLeagueByApiFootball(apiFootballId);

       return ResponseEntity.status(HttpStatus.CREATED).body(league);
    }
}
