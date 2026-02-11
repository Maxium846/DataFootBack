package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.dto.match.MatchDto;
import com.dataFoot.ProjetData.mapper.MatchMapper;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import com.dataFoot.ProjetData.service.ClassementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchController {


    private final MatchRepositoryInterface matchRepositoryInterface;

    private final ClassementService classementService;
    public MatchController(MatchRepositoryInterface matchRepositoryInterface, ClassementService classementService) {
        this.matchRepositoryInterface = matchRepositoryInterface;
        this.classementService = classementService;
    }

    @PostMapping("/{id}/score")
    public List<ClassementDto> updateScore(@PathVariable Long id,
                            @RequestParam int homeGoals,
                            @RequestParam int awayGoals) {
      return   classementService.updateMatchScoreAndRecalculate(id, homeGoals, awayGoals);
    }


    @GetMapping("/league/{leagueId}")
    public List<MatchDto> getMatchesByLeague(@PathVariable Long leagueId) {
        return matchRepositoryInterface.findMatchesByLeagueIdOrderByJourneeAsc(leagueId)
                .stream()
                .map(MatchMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public MatchDto getMatchById(@PathVariable Long id){
        Match match = matchRepositoryInterface.findById(id).orElseThrow();
        return MatchMapper.toDto(match);
    }


}
