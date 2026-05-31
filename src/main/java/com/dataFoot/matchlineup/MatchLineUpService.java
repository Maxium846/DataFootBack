package com.dataFoot.matchlineup;
import com.dataFoot.enumeration.Position;
import com.dataFoot.exception.entitexception.ExternalApiException;
import com.dataFoot.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.match.Match;
import com.dataFoot.matchlineup.dto.MatchLineUpDto;
import com.dataFoot.league.League;
import com.dataFoot.league.LeagueRepository;
import com.dataFoot.match.MatchRepository;
import com.dataFoot.matchlineup.mapper.MatchLineUpMapper;
import com.dataFoot.matchlineup.dtoapi.*;
import com.dataFoot.player.Player;
import com.dataFoot.player.PlayersRepository;
import com.dataFoot.team.Team;
import com.dataFoot.team.TeamRepository;
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
                              TeamRepository teamRepo, LeagueRepository leagueRepository, RestClient apiSportClient) {
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
                .orElseThrow(() -> new LeagueNotFoundException("La ligue avec l'id  : " + leagueId + " n'existe pas en base"));
        List<Match> matches = matchRepo.findByLeagueId(league.getId());


        int imported = 0;

        for (Match match : matches) {
            imported += importLineupsForMatch(match);
        }

        return imported + " compositions importées";
    }





    private int importLineupsForMatch(Match match) {
        if(lineupRepo.existsByMatchId(match.getId())){
            return 0;
        }
        String path = "/fixtures/lineups?fixture=" + match.getApiFootballFixtureId();
        ApiFootballLinupResponse response = callApi(path);


        if (response == null || response.getResponse() == null) {
            return 0;
        }

        List<MatchLineUp> lineupsToSave = new ArrayList<>();

        for (ApiFootballLineupItem lineup : response.getResponse()) {
            Team team = teamRepo.findByApiFootballTeamId(lineup.getTeam().getId())
                    .orElseThrow();

            addStarters(match, team, lineup, lineupsToSave);
            addSubstitutes(match, team, lineup, lineupsToSave);
        }

        lineupRepo.saveAll(lineupsToSave);

        return lineupsToSave.size();
    }
    private ApiFootballLinupResponse callApi(String path) {

        try {
            return apiSportClient.get().uri(path)
                    .retrieve()
                    .body(ApiFootballLinupResponse.class);
        } catch (RestClientException e) {

            throw new ExternalApiException("Erreur lors de l'appel API ou du parsing JSON",e);
        }
    }
    private void addStarters(
            Match match,
            Team team,
            ApiFootballLineupItem lineup,
            List<MatchLineUp> lineupsToSave
    ) {
        for (ApiFootballLineupStarter startXI : lineup.getStartXI()) {
            lineupsToSave.add(createLineUp(match, team, startXI, true));
        }
    }
    private void addSubstitutes(
            Match match,
            Team team,
            ApiFootballLineupItem lineup,
            List<MatchLineUp> lineupsToSave
    ) {
        for (ApiFootballLineupSubstitute substitute : lineup.getSubstitutes()) {
            lineupsToSave.add(createSubstitute(match, team, substitute, false));
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
    private MatchLineUp createLineUp(
            Match match,
            Team team,
            ApiFootballLineupStarter playerLineup,
            boolean starter
    ) {
        ApiFootballLineupPlayer playerDto = playerLineup.getPlayer();

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
    private MatchLineUp createSubstitute(
            Match match,
            Team team,
            ApiFootballLineupSubstitute playerLineup,
            boolean starter
    ) {
        ApiFootballLineupPlayer playerDto = playerLineup.getPlayer();

        Player player = playerRepo.findByApiFootballPlayerId(playerDto.getId())
                .orElseGet(Player::new);

        player.setApiFootballPlayerId(playerDto.getId());
        player.setName(playerDto.getName());

        Player savedPlayer = playerRepo.save(player);
        MatchLineUp sub = lineupRepo
                .findByMatchIdAndPlayersId(match.getId(), savedPlayer.getId())
                .orElseGet(MatchLineUp::new);


        sub.setTeam(team);
        sub.setMatch(match);
        sub.setPlayers(savedPlayer);
        sub.setPosition(mapPosition(playerDto.getPos()));
        sub.setStarter(starter);

        return sub;
    }
}
