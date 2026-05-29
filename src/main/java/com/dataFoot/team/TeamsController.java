package com.dataFoot.team;

import com.dataFoot.team.teamdto.ListTeamDto;
import com.dataFoot.team.teamdto.TeamDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamsController {
    private final TeamService teamService;

    public TeamsController(TeamService teamService) {
        this.teamService = teamService;
    }



    @GetMapping("/league/{leagueId}")
    public ResponseEntity<List<ListTeamDto>> getTeamsByLeagueId(@PathVariable long leagueId){

        List<ListTeamDto> listTeamDto = teamService.getTeamsByLeagueId(leagueId);

        return ResponseEntity.ok(listTeamDto);

    }

    @GetMapping("/{id}")
    public TeamDto getClubsById(@PathVariable Long id) {
        return teamService.findById(id);
    }

    @PostMapping("/{leagueId}/{season}")
    public ResponseEntity<List<TeamDto>> importTeams(@PathVariable Long leagueId, @PathVariable int season) {

        List<TeamDto> listeTeam = teamService.importOrUpdateClubsApiFootball(leagueId, season);
        return ResponseEntity.status(HttpStatus.CREATED).body(listeTeam);

    }


}
