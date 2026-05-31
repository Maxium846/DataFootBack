package com.datafoot.matchlineup;

import com.datafoot.enumeration.Position;
import com.datafoot.exception.entitexception.ExternalApiException;
import com.datafoot.exception.entitexception.LeagueNotFoundException;
import com.datafoot.exception.entitexception.TeamNotFoundException;
import com.datafoot.match.Match;
import com.datafoot.matchlineup.dto.MatchLineUpDto;
import com.datafoot.league.League;
import com.datafoot.league.LeagueRepository;
import com.datafoot.match.MatchRepository;
import com.datafoot.matchlineup.mapper.MatchLineUpMapper;
import com.datafoot.matchlineup.dtoapi.*;
import com.datafoot.player.Player;
import com.datafoot.player.PlayersRepository;
import com.datafoot.team.Team;
import com.datafoot.team.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchLineUpService {

    private final MatchLineUpRepository lineupRepo;
    private final MatchRepository matchRepo;
    private final PlayersRepository playerRepo;
    private final TeamRepository teamRepo;
    private final LeagueRepository leagueRepository;
    private final RestClient apiSportClient;

    public MatchLineUpService(MatchLineUpRepository lineupRepo,
                              MatchRepository matchRepo,
                              PlayersRepository playerRepo,
                              TeamRepository teamRepo,
                              LeagueRepository leagueRepository,
                              RestClient apiSportClient) {
        this.lineupRepo = lineupRepo;
        this.matchRepo = matchRepo;
        this.playerRepo = playerRepo;
        this.teamRepo = teamRepo;
        this.leagueRepository = leagueRepository;
        this.apiSportClient = apiSportClient;
    }

    public List<MatchLineUpDto> getLineUpByMatch(Long matchId) {
        return lineupRepo.findByMatchId(matchId)
                .stream()
                .map(MatchLineUpMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public String importLineUpForLeague(long leagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new LeagueNotFoundException(
                        "La ligue avec l'id : " + leagueId + " n'existe pas en base"));

        List<Match> matches = matchRepo.findByLeagueId(league.getId());

        int imported = 0;
        for (Match match : matches) {
            imported += importLineupsForMatch(match);
        }

        return imported + " compositions importées";
    }

    private int importLineupsForMatch(Match match) {
        if (lineupRepo.existsByMatchId(match.getId())) {
            return 0;
        }

        String path = "/fixtures/lineups?fixture=" + match.getApiFootballFixtureId();
        ApiFootballLineupResponse response = callApi(path);



        List<MatchLineUp> lineupsToSave = new ArrayList<>();

        for (ApiFootballLineupItem lineup : response.getResponse()) {
            Team team = teamRepo.findByApiFootballTeamId(lineup.getTeam().getId())
                    .orElseThrow(() -> new TeamNotFoundException(
                            "Team introuvable : " + lineup.getTeam().getId()));

            for (ApiFootballLineupStarter starter : lineup.getStartXI()) {
                lineupsToSave.add(createLineUpEntry(match, team, starter.getPlayer(), true));
            }

            for (ApiFootballLineupSubstitute substitute : lineup.getSubstitutes()) {
                lineupsToSave.add(createLineUpEntry(match, team, substitute.getPlayer(), false));
            }
        }

        lineupRepo.saveAll(lineupsToSave);
        return lineupsToSave.size();
    }

    private MatchLineUp createLineUpEntry(Match match, Team team,
                                          ApiFootballLineupPlayer playerDto, boolean starter) {
        Player player = playerRepo.findByApiFootballPlayerId(playerDto.getId())
                .orElseGet(Player::new);

        player.setApiFootballPlayerId(playerDto.getId());
        player.setName(playerDto.getName());
        Player savedPlayer = playerRepo.save(player);

        MatchLineUp lineUp = lineupRepo
                .findByMatchIdAndPlayersId(match.getId(), savedPlayer.getId())
                .orElseGet(MatchLineUp::new);

        lineUp.setTeam(team);
        lineUp.setMatch(match);
        lineUp.setPlayers(savedPlayer);
        lineUp.setPosition(mapPosition(playerDto.getPos()));
        lineUp.setStarter(starter);

        return lineUp;
    }

    private ApiFootballLineupResponse callApi(String path) {
        try {

            ApiFootballLineupResponse response = apiSportClient.get()
                    .uri(path)
                    .retrieve()
                    .body(ApiFootballLineupResponse.class);

            if(response == null || response.getResponse() == null){
                throw new ExternalApiException("Réponse API vide pour : " + path);
            }
            return  response;
        } catch (RestClientException e) {
            throw new ExternalApiException("Erreur lors de l'appel API ou du parsing JSON", e);
        }
    }

    public Position mapPosition(String pos) {
        if (pos == null || pos.isBlank()) {
            return null;
        }
        return switch (pos) {
            case "G" -> Position.GK;
            case "D" -> Position.DEF;
            case "M" -> Position.MID;
            case "F" -> Position.FWD;
            default -> null;
        };
    }
}