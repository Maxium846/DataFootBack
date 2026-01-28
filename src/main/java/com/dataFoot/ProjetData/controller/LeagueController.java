package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.league.LeagueAffichageDto;
import com.dataFoot.ProjetData.dto.league.LeagueByIdDto;
import com.dataFoot.ProjetData.dto.league.LeagueDto;
import com.dataFoot.ProjetData.service.LeagueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
public class LeagueController {

    private final LeagueService leagueService;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping
    public List<LeagueAffichageDto> getAllLeague (){

        return leagueService.findAll();
    }
    @PostMapping
    public LeagueDto createLeague(@RequestBody LeagueDto leagueDto){

        return leagueService.createLeague(leagueDto);

    }
    @GetMapping("/{id}")
    public LeagueByIdDto getLeagueById (@PathVariable Long id){

        return leagueService.getLeagueById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteLeague(@PathVariable Long id){
         leagueService.removeLeague(id);
    }
}
