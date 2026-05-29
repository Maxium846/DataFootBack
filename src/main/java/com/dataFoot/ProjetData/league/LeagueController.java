package com.dataFoot.ProjetData.league;

import com.dataFoot.ProjetData.league.dto.LeagueAffichageDto;
import com.dataFoot.ProjetData.league.dto.LeagueByIdDto;
import com.dataFoot.ProjetData.league.dto.LeagueDto;
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
    public ResponseEntity<List<LeagueAffichageDto>> getAllLeague() {

        return ResponseEntity.ok(leagueService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<LeagueByIdDto> getLeagueById(@PathVariable Long id) {

        LeagueByIdDto dto = leagueService.getLeagueById(id);
        return ResponseEntity.ok(dto);
    }
    @PostMapping("/import/{apiFootballId}")
    public ResponseEntity<LeagueAffichageDto> importLeagueByApiFootball(@PathVariable int apiFootballId){

        LeagueAffichageDto leagueAffichageDto = leagueService.importLeagueByApiFootball(apiFootballId);

       return ResponseEntity.status(HttpStatus.CREATED).body(leagueAffichageDto);
    }
}
