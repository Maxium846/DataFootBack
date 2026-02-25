package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.dto.match.MatchDto;
import com.dataFoot.ProjetData.mapper.MatchMapper;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.MatchRepository;
import com.dataFoot.ProjetData.service.ClassementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {


    private final MatchRepository matchRepository;

    private final ClassementService classementService;
    public MatchController(MatchRepository matchRepository, ClassementService classementService) {
        this.matchRepository = matchRepository;
        this.classementService = classementService;
    }

    @PostMapping("/{id}/score")
    public List<ClassementDto> updateScore(@PathVariable Long id,
                            @RequestParam int homeGoals,
                            @RequestParam int awayGoals) {
      return   classementService.updateMatchScoreAndRecalculate(id, homeGoals, awayGoals);
    }


    //ok
    @GetMapping("/league/{leagueId}")
    public List<MatchDto> getMatchesByLeague(@PathVariable Long leagueId) {
        return matchRepository.findMatchesByLeagueIdOrderByJourneeAsc(leagueId)
                .stream()
                .map(MatchMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public MatchDto getMatchById(@PathVariable Long id){
        Match match = matchRepository.findById(id).orElseThrow();
        return MatchMapper.toDto(match);
    }


}
