package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/generate/{leagueId}")
    public ResponseEntity<String> generateCalendar(@PathVariable Long leagueId) {
        try {
            calendarService.generateCalendar(leagueId);
            return ResponseEntity.ok("Calendrier généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur génération calendrier : " + e.getMessage());
        }
    }
}

