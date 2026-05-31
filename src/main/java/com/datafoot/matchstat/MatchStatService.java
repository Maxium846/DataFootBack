package com.datafoot.matchstat;

import com.datafoot.exception.entitexception.ExternalApiException;
import com.datafoot.exception.entitexception.LeagueNotFoundException;
import com.datafoot.exception.entitexception.MatchNotFoundException;
import com.datafoot.exception.entitexception.TeamNotFoundException;
import com.datafoot.league.League;
import com.datafoot.league.LeagueRepository;
import com.datafoot.matchstat.dto.MatchstatDto;
import com.datafoot.matchstat.dtoapi.ApiFootballMatchStatItems;
import com.datafoot.matchstat.dtoapi.ApiFootballMatchStatResponse;
import com.datafoot.matchstat.dtoapi.ApiFootballMatchStatStatistique;
import com.datafoot.matchstat.mapper.MatchStatMapper;
import com.datafoot.match.Match;
import com.datafoot.team.Team;
import com.datafoot.team.TeamRepository;
import com.datafoot.match.MatchRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchStatService {



    private final MatchRepository matchRepository;

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final MatchStatRepository matchStatRepository;

    private final RestClient  apiSportClient;


    public MatchStatService(MatchRepository matchRepository, LeagueRepository leagueRepository, TeamRepository teamRepository, MatchStatRepository matchStatRepository, RestClient apiSportClient) {
        this.matchRepository = matchRepository;
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.matchStatRepository = matchStatRepository;
        this.apiSportClient = apiSportClient;
    }



    public List<MatchstatDto> getStatMatch (long matchId){

        Match match = matchRepository.findById(matchId).orElseThrow(()->new MatchNotFoundException("Le matche n'existe pas en base"));

        return matchStatRepository.findByMatchId(match.getId()).stream().map(MatchStatMapper::toInDto).toList();

    }



    @Transactional
    public int importStatMatch(long leagueId){

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new LeagueNotFoundException("La ligue avec l'id  : " + leagueId + " n'existe pas en base"));

        List<Match> matches = matchRepository.findByLeagueId(league.getId());

        int importStat = 0;
        for(Match match : matches){
            if(matchStatRepository.existsByMatchId(match.getId())){
                continue;
            }
            List<MatchStat> listMatchStat = new ArrayList<>();

            String path = "/fixtures/statistics?fixture=" + match.getApiFootballFixtureId();
            ApiFootballMatchStatResponse response = callApi(path);


            for(ApiFootballMatchStatItems items : response.getResponse()){

                Team team = teamRepository.findByApiFootballTeamId(items.getTeam().getId()).orElseThrow(()->new TeamNotFoundException("La team n'existe pas"));


                MatchStat matchStats = new MatchStat();

                matchStats.setMatch(match);
                matchStats.setTeamId(team);
                for(ApiFootballMatchStatStatistique stat : items.getStatistics()) {

                    String type = stat.getType();
                    JsonNode value = stat.getValue();

                    switch (type) {

                        case "Shots on Goal" ->
                                matchStats.setShootsOnGoals(toInteger(value));

                        case "Shots off Goal" ->
                                matchStats.setShootOffGoals(toInteger(value));

                        case "Total Shots" ->
                                matchStats.setTotalShots(toInteger(value));

                        case "Blocked Shots" ->
                                matchStats.setBlockedShots(toInteger(value));

                        case "Shots insidebox" ->
                                matchStats.setShotInsideBox(toInteger(value));

                        case "Shots outsidebox" ->
                                matchStats.setShotsOutsideBox(toInteger(value));

                        case "Fouls" ->
                                matchStats.setFouls(toInteger(value));

                        case "Corner Kicks" ->
                                matchStats.setCornerKick(toInteger(value));

                        case "Offsides" ->
                                matchStats.setOffsides(toInteger(value));

                        case "Ball Possession" ->
                                matchStats.setBallPossession(toPercentInteger(value));

                        case "Yellow Cards" ->
                                matchStats.setYellowCards(toInteger(value));

                        case "Red Cards" ->
                                matchStats.setRedCards(toInteger(value));

                        case "Goalkeeper Saves" ->
                                matchStats.setGoalkeeperSave(toInteger(value));

                        case "Total passes" ->
                                matchStats.setTotalPasses(toInteger(value));

                        case "Passes accurate" ->
                                matchStats.setPassesAccurate(toInteger(value));

                        case "Passes %" ->
                                matchStats.setPassesPercentage(toPercentInteger(value));

                        case "expected_goals" ->
                                matchStats.setExpectedGoals(toDouble(value));

                        case "goals_prevented" ->
                                matchStats.setGoalsPrevented(toDouble(value));
                    }
                }
                listMatchStat.add(matchStats);
                importStat++;

            }
            matchStatRepository.saveAll(listMatchStat);

        }

        return importStat;
    }


    private ApiFootballMatchStatResponse callApi(String path) {

        try {
            ApiFootballMatchStatResponse response = apiSportClient.get()
                    .uri(path)
                    .retrieve()
                    .body(ApiFootballMatchStatResponse.class);

            if(response == null || response.getResponse() == null){
                throw new ExternalApiException("Réponse API vide pour : " + path);
            }

            return response;

        } catch (RestClientException e) {
            throw new ExternalApiException(
                    "Erreur lors de l'appel API ou du parsing JSON",
                    e
            );
        }
    }

    private Integer toInteger(JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        }

        if (value.isNumber()) {
            return value.asInt();
        }

        if (value.isTextual()) {
            String text = value.asText().replace("%", "").trim();

            if (text.isBlank()) {
                return null;
            }

            return Integer.parseInt(text);
        }

        return null;
    }

    private Double toDouble(JsonNode value) {

        if(value == null || value.isNull()){
            return null;
        }

        if(value.isNumber()){
            return value.asDouble();
        }

        if(value.isTextual()){
            return Double.parseDouble(value.asText());
        }

        return null;
    }
    private Integer toPercentInteger(JsonNode value) {
        return toInteger(value);
    }


}
