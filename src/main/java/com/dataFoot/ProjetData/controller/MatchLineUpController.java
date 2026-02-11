package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.match.MatchLineUpDto;
import com.dataFoot.ProjetData.service.MatchLineUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchLineUpController {

    private final MatchLineUpService lineupService;

    public MatchLineUpController(MatchLineUpService lineupService) {
        this.lineupService = lineupService;
    }

    // 🔹 Récupérer la composition d'un match
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchLineUpDto>> getLineUpByMatch(@PathVariable Long matchId) {
        List<MatchLineUpDto> lineups = lineupService.getLineUpByMatch(matchId);
        return ResponseEntity.ok(lineups);
    }

    // 🔹 Ajouter un joueur dans la composition d'un match
    @PostMapping("/{matchId}/lineup")    public ResponseEntity<List<MatchLineUpDto>> addLineups(
            @PathVariable Long matchId,
            @RequestBody List<MatchLineUpDto> lineups) {

        List<MatchLineUpDto> saved = lineupService.saveLineups(matchId, lineups);
        return ResponseEntity.ok(saved);
    }
}
