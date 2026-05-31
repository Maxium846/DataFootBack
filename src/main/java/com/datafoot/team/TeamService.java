package com.datafoot.team;

import com.datafoot.exception.entitexception.ExternalApiException;
import com.datafoot.exception.entitexception.LeagueNotFoundException;
import com.datafoot.exception.entitexception.TeamNotFoundException;
import com.datafoot.team.teamdto.ListTeamDto;
import com.datafoot.team.teamdto.TeamDto;
import com.datafoot.league.League;
import com.datafoot.league.LeagueRepository;
import com.datafoot.team.mapper.TeamMapper;
import com.datafoot.team.teamdtoapi.ResponseApiItemsDtoTeam;
import com.datafoot.team.teamdtoapi.ResponseApiTeamsDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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

        List<Team> teams = teamRepository.findByLeagueId(leagueId);
        if (teams.isEmpty()) {
            throw new LeagueNotFoundException("Aucun club n'a été trouvé pour la ligue avec L'id " + leagueId);
        }
        else {

            return teams.stream().map(TeamMapper::toListDto).toList();

        }
    }


    public TeamDto findById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Club non trouvé"));
        return TeamMapper.toDto(team);
    }

    @Transactional
    public List<TeamDto> importOrUpdateClubsApiFootball(Long leagueId, int season) {
        List<Team> listTeams = new ArrayList<>();

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new LeagueNotFoundException("La ligue avec l'id  : " + leagueId + " n'existe pas en base"));
        Integer apiFootballLeagueId = league.getApiFootballLeagueId();
        if (apiFootballLeagueId == null) {
            throw new LeagueNotFoundException("la ligue avec l'id" + leagueId + "n'a pas de  correspondant en base ");
        }

        String path = "/teams?league=" + apiFootballLeagueId + "&season=" + season;

        ResponseApiTeamsDto responseTeamsDto = callApi(path);
        if (responseTeamsDto == null || responseTeamsDto.getResponse() == null || responseTeamsDto.getResponse().isEmpty()) {

            throw new TeamNotFoundException("Aucun club n'a été trouvé pour cette ligue et cette saison");
        }

        for (ResponseApiItemsDtoTeam dto : responseTeamsDto.getResponse()) {


            Team team = teamRepository.findByLeagueIdAndApiFootballTeamId(leagueId,dto.getTeam().getId()).orElseGet(Team::new);

            updateTeamFromApi(team, dto, league);

            listTeams.add(team);
        }
        teamRepository.saveAll(listTeams);
        return listTeams.stream().map(TeamMapper::toDto).toList();
    }

    private ResponseApiTeamsDto callApi(String path) {

        try {
            return apiSportClient.get().uri(path)
                    .retrieve()
                    .body(ResponseApiTeamsDto.class);
        } catch (RestClientException e) {

            throw new ExternalApiException("Erreur lors de l'appel API ou du parsing JSON",e);
        }
    }

    public static void updateTeamFromApi (Team team , ResponseApiItemsDtoTeam dto ,League league){

        team.setApiFootballTeamId(dto.getTeam().getId());
        team.setLogo(dto.getTeam().getLogo());
        team.setName(dto.getTeam().getName());
        team.setLeague(league);
        team.setFounded(dto.getTeam().getFounded());
        team.setImage(dto.getVenue().getImage());
        team.setCity(dto.getVenue().getCity());
        team.setSurface(dto.getVenue().getSurface());
        team.setNameStadium(dto.getVenue().getName());

    }
}








