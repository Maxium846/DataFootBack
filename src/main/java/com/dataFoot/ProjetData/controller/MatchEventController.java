package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.match.MatchEventDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.service.MatchEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match-events")
public class MatchEventController {

    private final MatchEventService eventService;

    public MatchEventController(MatchEventService eventService) {
        this.eventService = eventService;
    }

    // 🔹 Récupérer tous les événements d'un joueur
    @GetMapping("/player/{playerId}/stat")
    public ResponseEntity<PlayerStatDto> getEventsByMatch(@PathVariable Long playerId) {
        PlayerStatDto events = eventService.getPlayerStats(playerId);
        return ResponseEntity.ok(events);
    }


    // 🔹 Ajouter un événement (but, passe, carton, etc.)
    @PostMapping
    public ResponseEntity<MatchEventDto> addEvent(@RequestBody MatchEventDto dto) {
        MatchEventDto saved = eventService.saveEvent(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchEventDto>> getEventsByMatchId(@PathVariable Long matchId) {
        return ResponseEntity.ok(eventService.getEventsByMatchId(matchId));
    }

}
