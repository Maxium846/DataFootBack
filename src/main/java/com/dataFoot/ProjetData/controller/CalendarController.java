package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.service.CalendarService;
import com.dataFoot.ProjetData.service.FixtureImportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

private final FixtureImportService fixtureImportService;

    public CalendarController( FixtureImportService fixtureImportService) {
        this.fixtureImportService = fixtureImportService;
    }

    @PostMapping("/generate-from-pl/{leagueId}")
    public String generateFromPL(@PathVariable Long leagueId) {
        return fixtureImportService.generateCalendarFromApiFootball(leagueId);
    }

}
