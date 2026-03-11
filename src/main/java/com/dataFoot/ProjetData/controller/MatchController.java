package com.dataFoot.ProjetData.controller;
import com.dataFoot.ProjetData.dto.match.MatchDto;
import com.dataFoot.ProjetData.mapper.MatchMapper;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.MatchRepository;
import com.dataFoot.ProjetData.service.FixtureImportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {


    private final MatchRepository matchRepository;


    private final FixtureImportService fixtureImportService;
    public MatchController(MatchRepository matchRepository, FixtureImportService fixtureImportService) {
        this.matchRepository = matchRepository;
        this.fixtureImportService = fixtureImportService;
    }


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


    @PostMapping("/generate/{leagueId}")
    public String generateFromPL(@PathVariable Long leagueId) {
        return fixtureImportService.generateCalendarFromApiFootball(leagueId);
    }}
