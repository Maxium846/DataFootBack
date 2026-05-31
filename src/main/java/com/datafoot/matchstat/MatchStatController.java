package com.datafoot.matchstat;

import com.datafoot.matchstat.dto.MatchstatDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchStat")
public class MatchStatController {

    private final MatchStatService matchStatService;

    public MatchStatController(MatchStatService matchStatService) {
        this.matchStatService = matchStatService;
    }

    @PostMapping("/{leagueId}")
    public int importMatchStat(@PathVariable Long leagueId) throws Exception {


        return matchStatService.importStatMatch(leagueId);
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity< List<MatchstatDto> >getMatchStat (@PathVariable long matchId){

         List<MatchstatDto> matchstatDto = matchStatService.getStatMatch(matchId);

         return ResponseEntity.ok(matchstatDto);

    }

}
