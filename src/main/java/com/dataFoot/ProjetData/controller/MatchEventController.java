package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.match.MatchEventDto;
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

    // 🔹 Récupérer tous les événements d'un match
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchEventDto>> getEventsByMatch(@PathVariable Long matchId) {
        List<MatchEventDto> events = eventService.getEventsByMatch(matchId);
        return ResponseEntity.ok(events);
    }

    // 🔹 Ajouter un événement (but, passe, carton, etc.)
    @PostMapping
    public ResponseEntity<MatchEventDto> addEvent(@RequestBody MatchEventDto dto) {
        MatchEventDto saved = eventService.saveEvent(dto);
        return ResponseEntity.ok(saved);
    }
}
