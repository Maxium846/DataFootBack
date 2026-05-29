package com.dataFoot.ProjetData.team;

import com.dataFoot.ProjetData.exception.entitexception.ExternalApiException;
import com.dataFoot.ProjetData.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.ProjetData.exception.entitexception.TeamNotFoundException;
import com.dataFoot.ProjetData.team.teamdto.ListTeamDto;
import com.dataFoot.ProjetData.team.teamdto.TeamDto;
import com.dataFoot.ProjetData.league.League;
import com.dataFoot.ProjetData.league.LeagueRepository;
import com.dataFoot.ProjetData.team.mapper.TeamMapper;
import com.dataFoot.ProjetData.team.teamdtoapi.ResponseApiItemsDtoTeam;
import com.dataFoot.ProjetData.team.teamdtoapi.ResponseApiTeamsDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;
    private final RestClient apiSportClient;


    public TeamService(TeamRepository teamRepository, LeagueRepository leagueRepository, RestClient apiSportClient) {
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
        this.apiSportClient = apiSportClient;
    }

    public List<TeamDto> findAll() {
        return teamRepository.findAll()
                .stream()
                .map(TeamMapper::toDto)
                .toList();
    }

    public List<ListTeamDto> getTeamsByLeagueId(long leagueId) {

        List<Teams> teams = teamRepository.findByLeagueId(leagueId);
        if (teams.isEmpty()) {
            throw new LeagueNotFoundException("Aucun club n'a été trouvé pour cette league" +   leagueId);
        } else {
            return teams.stream().map(TeamMapper::toListDto).toList();

        }
    }

    public TeamDto findById(Long id) {
        Teams teams = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Club non trouvé"));
        return TeamMapper.toDto(teams);
    }

    @Transactional
    public List<TeamDto> importOrUpdateClubsApiFootball(Long leagueId, int season) {
        List<Teams> listTeams = new ArrayList<>();

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new LeagueNotFoundException("La ligue avec l'id  : " + leagueId + " n'existe pas"));
        Integer apiFootballLeagueId = league.getApiFootballLeague();
        if (apiFootballLeagueId == null) {
            throw new LeagueNotFoundException("la ligue avec l'id" + leagueId + "n'a pas de  correspondant en base ");
        }
        String path = "/teams?league=" + apiFootballLeagueId + "&season=" + season;

        ResponseApiTeamsDto responseTeamsDto = callApi(path);
        if (responseTeamsDto == null || responseTeamsDto.getResponse() == null || responseTeamsDto.getResponse().isEmpty()) {

            throw new TeamNotFoundException("Aucun club n'a été importer");
        }

        for (ResponseApiItemsDtoTeam dto : responseTeamsDto.getResponse()) {


            Teams teams = teamRepository.findByLeagueIdAndApiFootballTeamId(leagueId,dto.getTeam().getId()).orElseGet(Teams ::new);

            Teams result =TeamMapper.toUpdateEntity(teams, dto, league);

            listTeams.add(result);
        }
        teamRepository.saveAll(listTeams);
        return listTeams.stream().map(TeamMapper::toDto).toList();
    }

    private ResponseApiTeamsDto callApi(String path) {

        try {
            return apiSportClient.get().uri(path)
                    .retrieve()
                    .body(ResponseApiTeamsDto.class);
        } catch (Exception e) {

            throw new ExternalApiException("Erreur lors de l'appel API ou du parsing JSON",e);
        }
    }
}








