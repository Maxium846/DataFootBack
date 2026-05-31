package com.dataFoot.match;
import com.dataFoot.match.matchdto.MatchDto;
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


    @PostMapping("/generate/{leagueId}/{season}")
    public String importMatch(@PathVariable Long leagueId ,@PathVariable int season) {
        return fixtureImportService.generateCalendarFromApiFootball(leagueId,season);
    }}
