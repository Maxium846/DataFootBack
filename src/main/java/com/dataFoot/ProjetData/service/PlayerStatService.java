package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatMatchDto;
import com.dataFoot.ProjetData.mapper.PlayerStatMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.model.PlayerStats;
import com.dataFoot.ProjetData.repository.ClubRepository;
import com.dataFoot.ProjetData.repository.MatchRepository;
import com.dataFoot.ProjetData.repository.PlayerStatRepository;
import com.dataFoot.ProjetData.repository.PlayersRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PlayerStatService {


    private final PlayerStatRepository playerStatRepository;
    private final PlayersRepository playersRepository;
    private final MatchRepository matchRepository;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final  ClubRepository clubRepository;
    private final ObjectMapper objectMapper;

    public PlayerStatService(PlayerStatRepository playerStatRepository, PlayersRepository playersRepository, PlayersRepository playersRepository1, MatchRepository matchRepository, ClubRepository clubRepository, ObjectMapper objectMapper) {
        this.playerStatRepository = playerStatRepository;
        this.playersRepository = playersRepository1;
        this.matchRepository = matchRepository;
        this.clubRepository = clubRepository;
        this.objectMapper = objectMapper;
    }


    @Value("${apisports.key}")
    private String apiSportsKey;
    public PlayerStatMatchDto getStatByJoueurId(Long id){

        Player player = playersRepository.findById(id).orElseThrow(()-> new RuntimeException("l'id du jouuer n'existe pas"));

        PlayerStats playerStats = playerStatRepository.findByPlayer_Id(player.getId());

        return PlayerStatMapper.toDto(playerStats);


    }

    public void importStatPlayer(Long leagueId) throws Exception {

        List<Match> matches = matchRepository.findByLeagueId(leagueId);

        for (Match match : matches) {

            int fixtureId = match.getApiFootballFixtureId();
            String url = "https://v3.football.api-sports.io/fixtures/players?fixture=" + fixtureId;
            JsonNode response = callApi(url).path(("response"));

            for (JsonNode json : response) {

                Long apiteamId = json.path("team").path("id").isMissingNode() ? null : json.path("team").path("id").asLong();
                if (apiteamId == null) continue;
                Club club = clubRepository.findByLeagueId(leagueId).stream().filter(c -> Objects.equals(c.getApiFootballTeamId(),apiteamId)).findFirst().orElse(null);

                for (JsonNode playerNode : json.path("players")) {

                    Integer apiPlayerId = playerNode.path("player").path("id").isMissingNode() ? null : playerNode.path("player").path("id").asInt();
                    String apiPlayerName = playerNode.path("player").path("name").isMissingNode() ? null : playerNode.path("player").path("name").asText();
                if (apiPlayerId == null) continue;
                Player player = playersRepository.findByApiFootballPlayerId(apiPlayerId).orElseThrow(null);
                    if (player == null) {
                        System.out.println("Joueur introuvable en base : " + apiPlayerId + " - " + apiPlayerName);
                        continue;
                    }

                    JsonNode statNode = playerNode.path("statistics").get(0);
                    if (statNode == null || statNode.isMissingNode() || statNode.isNull()) {
                        continue;
                    }

                    // games
                    Integer apiMinutePlayed = statNode.path("games").path("minutes").isNull()
                            ? null : statNode.path("games").path("minutes").asInt();
                    String apiRating = statNode.path("games").path("rating").isNull()
                            ? null : statNode.path("games").path("rating").asText();
                    Boolean apiCaptain = statNode.path("games").path("captain").isNull()
                            ? null : statNode.path("games").path("captain").asBoolean();
                    Boolean substitute = statNode.path("games").path("substitute").isNull()
                            ? null : statNode.path("games").path("substitute").asBoolean();

                    // offsides
                    Integer offsides = statNode.path("offsides").isNull()
                            ? null : statNode.path("offsides").asInt();

                    // shots
                    Integer totalShoot = statNode.path("shots").path("total").isNull()
                            ? null : statNode.path("shots").path("total").asInt();
                    Integer shootOnTarget = statNode.path("shots").path("on").isNull()
                            ? null : statNode.path("shots").path("on").asInt();

                    // goals
                    Integer totalGoal = statNode.path("goals").path("total").isNull()
                            ? null : statNode.path("goals").path("total").asInt();
                    Integer conceded = statNode.path("goals").path("conceded").isNull()
                            ? null : statNode.path("goals").path("conceded").asInt();
                    Integer assists = statNode.path("goals").path("assists").isNull()
                            ? null : statNode.path("goals").path("assists").asInt();
                    Integer saves = statNode.path("goals").path("saves").isNull()
                            ? null : statNode.path("goals").path("saves").asInt();

                    // passes
                    Integer totalPass = statNode.path("passes").path("total").isNull()
                            ? null : statNode.path("passes").path("total").asInt();
                    Integer key = statNode.path("passes").path("key").isNull()
                            ? null : statNode.path("passes").path("key").asInt();
                    String accuracy = statNode.path("passes").path("accuracy").isNull()
                            ? null : statNode.path("passes").path("accuracy").asText();

                    // tackles
                    Integer totalTackles = statNode.path("tackles").path("total").isNull()
                            ? null : statNode.path("tackles").path("total").asInt();
                    Integer blocks = statNode.path("tackles").path("blocks").isNull()
                            ? null : statNode.path("tackles").path("blocks").asInt();
                    Integer interceptions = statNode.path("tackles").path("interceptions").isNull()
                            ? null : statNode.path("tackles").path("interceptions").asInt();

                    // duels
                    Integer totalDuels = statNode.path("duels").path("total").isNull()
                            ? null : statNode.path("duels").path("total").asInt();
                    Integer won = statNode.path("duels").path("won").isNull()
                            ? null : statNode.path("duels").path("won").asInt();

                    // dribbles
                    Integer attempts = statNode.path("dribbles").path("attempts").isNull()
                            ? null : statNode.path("dribbles").path("attempts").asInt();
                    Integer success = statNode.path("dribbles").path("success").isNull()
                            ? null : statNode.path("dribbles").path("success").asInt();
                    Integer past = statNode.path("dribbles").path("past").isNull()
                            ? null : statNode.path("dribbles").path("past").asInt();

                    // fouls
                    Integer drawn = statNode.path("fouls").path("drawn").isNull()
                            ? null : statNode.path("fouls").path("drawn").asInt();
                    Integer committed = statNode.path("fouls").path("committed").isNull()
                            ? null : statNode.path("fouls").path("committed").asInt();

                    // cards
                    Integer yellow = statNode.path("cards").path("yellow").isNull()
                            ? null : statNode.path("cards").path("yellow").asInt();
                    Integer red = statNode.path("cards").path("red").isNull()
                            ? null : statNode.path("cards").path("red").asInt();

                    // penalty
                    Integer penaltyWon = statNode.path("penalty").path("won").isNull()
                            ? null : statNode.path("penalty").path("won").asInt();
                    Integer penaltycommited = statNode.path("penalty").path("commited").isNull()
                            ? null : statNode.path("penalty").path("commited").asInt();
                    Integer scored = statNode.path("penalty").path("scored").isNull()
                            ? null : statNode.path("penalty").path("scored").asInt();
                    Integer missed = statNode.path("penalty").path("missed").isNull()
                            ? null : statNode.path("penalty").path("missed").asInt();
                    Integer saved = statNode.path("penalty").path("saved").isNull()
                            ? null : statNode.path("penalty").path("saved").asInt();

                    PlayerStats stat = new PlayerStats();
                    stat.setClub(club);
                    stat.setMatch(match);
                    stat.setPlayer(player);
                    assert club != null;
                    stat.setNameClub(club.getName());
                    stat.setNameJoueur(apiPlayerName);
                    stat.setMinutePlayed(apiMinutePlayed != null ? apiMinutePlayed : 0);
                    stat.setNote(apiRating);
                    stat.setCaptain(apiCaptain != null ? apiCaptain : false);
                    stat.setSubstitute(substitute != null ? substitute : false);

                    stat.setOffside(offsides != null ? offsides : 0);

                    stat.setTotalShoot(totalShoot != null ? totalShoot : 0);
                    stat.setShootOnTarget(shootOnTarget != null ? shootOnTarget : 0);

                    stat.setTotalGoal(totalGoal != null ? totalGoal : 0);
                    stat.setGoalConceded(conceded != null ? conceded : 0);
                    stat.setAssist(assists != null ? assists : 0);
                    stat.setSaves(saves != null ? saves : 0);

                    stat.setTotalPasse(totalPass != null ? totalPass : 0);
                    stat.setKeyPasse(key != null ? key : 0);
                    stat.setAccuracyPass(accuracy);

                    stat.setTotalTackle(totalTackles != null ? totalTackles : 0);
                    stat.setBlocks(blocks != null ? blocks : 0);
                    stat.setInterception(interceptions != null ? interceptions : 0);

                    stat.setTotalDuels(totalDuels != null ? totalDuels : 0);
                    stat.setWonDuels(won != null ? won : 0);

                    stat.setAttemptsDribbles(attempts != null ? attempts : 0);
                    stat.setSucessDribles(success != null ? success : 0);
                    stat.setPastDribbles(past != null ? past : 0);

                    stat.setFoulsDrawns(drawn != null ? drawn : 0);
                    stat.setFoulsCommitted(committed != null ? committed : 0);

                    stat.setYellowCard(yellow != null ? yellow : 0);
                    stat.setRedCard(red != null ? red : 0);

                    stat.setPenaltyWon(penaltyWon != null ? penaltyWon : 0);
                    stat.setPenaltyCommited(penaltycommited != null ? penaltycommited : 0);
                    stat.setPenaltyScored(scored != null ? scored : 0);
                    stat.setPenaltyMissed(missed != null ? missed : 0);
                    stat.setPenaltSaved(saved != null ? saved : 0);

                    playerStatRepository.save(stat);
                }
            }
        }
    }


    private JsonNode callApi(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("x-apisports-key", apiSportsKey)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) throw new RuntimeException("API error " + resp.statusCode());
        return objectMapper.readTree(resp.body());
    }

    private Integer getInteger(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }

    private String getString(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private boolean getBoolean(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return !value.isMissingNode() && !value.isNull() && value.asBoolean();
    }
}


