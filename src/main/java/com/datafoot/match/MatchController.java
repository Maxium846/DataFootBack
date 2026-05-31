package com.datafoot.match;
import com.datafoot.match.matchdto.MatchDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

private final MatchService matchService;



    private final MatchImportService matchImportService;
    public MatchController( MatchService matchService, MatchImportService matchImportService) {
        this.matchService = matchService;
        this.matchImportService = matchImportService;
    }


    @GetMapping("/league/{leagueId}")
    public List<MatchDto> getMatchesByLeague(@PathVariable Long leagueId) {
        return matchService.getMatchesByLeague(leagueId);
    }

    @GetMapping("/{id}")
    public MatchDto getMatchById(@PathVariable Long id){
        MatchDto matchDto = new MatchDto();

        return matchService.getMatchById(id);
    }


    @PostMapping("/generate/{leagueId}/{season}")
    public String importMatch(@PathVariable Long leagueId ,@PathVariable int season) {
        return matchImportService.generateCalendarFromApiFootball(leagueId,season);
    }}
