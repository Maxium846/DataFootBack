package com.datafoot.matchevent;

import com.datafoot.match.matchdto.MatchEventDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MatchEventController {

    private final MatchEventService eventService;

    public MatchEventController(MatchEventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchEventDto>> getEventsByMatchId(@PathVariable Long matchId) {
        return ResponseEntity.ok(eventService.getEventsByMatchId(matchId));
    }

    @PostMapping("/event/{leagueId}")
    public String importEventMatch(@PathVariable Long leagueId) {
        return eventService.importEventByMatch(leagueId);
    }




}
