package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.dto.match.MatchDto;
import com.dataFoot.ProjetData.mapper.MatchMapper;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import com.dataFoot.ProjetData.service.ClassementService;
import com.dataFoot.ProjetData.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {


    private final MatchService matchService;
    private final MatchRepositoryInterface matchRepositoryInterface;

    private final ClassementService classementService;
    public MatchController(MatchService matchService, MatchRepositoryInterface matchRepositoryInterface, ClassementService classementService) {
        this.matchService = matchService;
        this.matchRepositoryInterface = matchRepositoryInterface;
        this.classementService = classementService;
    }

    @PostMapping("/{id}/score")
    public List<ClassementDto> updateScore(@PathVariable Long id,
                            @RequestParam int homeGoals,
                            @RequestParam int awayGoals) {
      return   classementService.updateMatchScoreAndRecalculate(id, homeGoals, awayGoals);
    }

    @PostMapping("/league/{leagueId}/generate-calendar")
    public ResponseEntity<Void> generateCalendar(@PathVariable Long leagueId) {
        matchService.generateCalendar(leagueId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/league/{leagueId}")
    public List<MatchDto> getMatchesByLeague(@PathVariable Long leagueId) {
        return matchRepositoryInterface.findByLeagueIdOrderByJourneeAsc(leagueId)
                .stream()
                .map(MatchMapper::toDto)
                .toList();
    }

}
