package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.match.MatchstatDto;
import com.dataFoot.ProjetData.service.MatchStatService;
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
    public void importMatchStat(@PathVariable Long leagueId) throws Exception {


        matchStatService.importStatMatch(leagueId);
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity< List<MatchstatDto> >getMatchStat (@PathVariable long matchId){

         List<MatchstatDto> matchstatDto = matchStatService.getStatMatch(matchId);

         return ResponseEntity.ok(matchstatDto);

    }

}
