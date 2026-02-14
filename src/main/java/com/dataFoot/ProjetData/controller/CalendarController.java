package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.service.CalendarService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {


private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }


    @PostMapping("/generate-from-pl/{leagueId}")
    public String generateFromPL(@PathVariable Long leagueId) {
        return calendarService.generateFromPL(leagueId);
    }

}
