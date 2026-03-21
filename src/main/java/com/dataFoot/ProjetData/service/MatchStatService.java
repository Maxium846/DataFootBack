package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.MatchstatDto;
import com.dataFoot.ProjetData.exception.RateLimitException;
import com.dataFoot.ProjetData.mapper.MatchStatMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.model.MatchStat;
import com.dataFoot.ProjetData.repository.ClubRepository;
import com.dataFoot.ProjetData.repository.MatchRepository;
import com.dataFoot.ProjetData.repository.MatchStatRepository;
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
public class MatchStatService {


    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://v3.football.api-sports.io";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final MatchRepository matchRepository;

    private final ClubRepository clubRepository;
    private final MatchStatRepository matchStatRepository;

    @Value("${apisports.key}")
    private String apiSportsKey;

    public MatchStatService(ObjectMapper objectMapper, MatchRepository matchRepository, ClubRepository clubRepository, MatchStatRepository matchStatRepository) {
        this.objectMapper = objectMapper;
        this.matchRepository = matchRepository;
        this.clubRepository = clubRepository;
        this.matchStatRepository = matchStatRepository;
    }



    public List<MatchstatDto> getStatMatch (long matchId){

        Match match = matchRepository.findById(matchId).orElseThrow();

        return matchStatRepository.findByMatchId(match.getId()).stream().map(MatchStatMapper::toInDto).toList();

    }
public void importStatMatch(Long leagueId) throws Exception {


        List<Match> matches = matchRepository.findByLeagueId(leagueId);

        for(Match match : matches) {

            int fixtureId = match.getApiFootballFixtureId();

            String url ="https://v3.football.api-sports.io/fixtures/statistics?fixture=" + fixtureId ;
           JsonNode reponse = callApiWithRetry(url).path("response");


            for (JsonNode json : reponse){

                Long apiteamId = json.path("team").path("id").isMissingNode()? null: json.path("team").path("id").asLong();
                if(apiteamId == null) continue;
                Club clubs = clubRepository.findByLeagueId(leagueId).stream().filter(c -> Objects.equals(c.getApiFootballTeamId(),apiteamId)).findFirst().orElse(null);
                if (clubs == null) continue;
                JsonNode statsArray = json.path("statistics");
                Map<String, JsonNode> stats = new HashMap<>();

                if (statsArray.isArray()) {
                    for (JsonNode s : statsArray) {
                        String type = s.path("type").asText(null);
                        JsonNode value = s.get("value"); // peut être null
                        if (type != null) stats.put(type, value);
                    }
                }

                Integer fouls = stats.get("Fouls") == null || stats.get("Fouls").isNull() ? null : stats.get("Fouls").asInt();
                Integer apiShootOnGoals =
                        stats.get("Shots on Goal") == null || stats.get("Shots on Goal").isNull()
                                ? null : stats.get("Shots on Goal").asInt();

                Integer apishotsOffGoals =
                        stats.get("Shots off Goal") == null || stats.get("Shots off Goal").isNull()
                                ? null : stats.get("Shots off Goal").asInt();

                Integer totalShoot =
                        stats.get("Total Shots") == null || stats.get("Total Shots").isNull()
                                ? null : stats.get("Total Shots").asInt();

                Integer blockedShoot =
                        stats.get("Blocked Shots") == null || stats.get("Blocked Shots").isNull()
                                ? null : stats.get("Blocked Shots").asInt();

                Integer shotsInsideBox =
                        stats.get("Shots insidebox") == null || stats.get("Shots insidebox").isNull()
                                ? null : stats.get("Shots insidebox").asInt();

                Integer shootOutsideBox =
                        stats.get("Shots outsidebox") == null || stats.get("Shots outsidebox").isNull()
                                ? null : stats.get("Shots outsidebox").asInt();

                Integer cornerKick =
                        stats.get("Corner Kicks") == null || stats.get("Corner Kicks").isNull()
                                ? null : stats.get("Corner Kicks").asInt();

                Integer offsides =
                        stats.get("Offsides") == null || stats.get("Offsides").isNull()
                                ? null : stats.get("Offsides").asInt();

                Integer ballPossession =
                        stats.get("Ball Possession") == null || stats.get("Ball Possession").isNull()
                                ? null
                                : Integer.parseInt(stats.get("Ball Possession").asText().replace("%", ""));

                Integer passePercentage =
                        stats.get("Passes %") == null || stats.get("Passes %").isNull()
                                ? null
                                : Integer.parseInt(stats.get("Passes %").asText().replace("%", ""));

                Integer yellowCards =
                        stats.get("Yellow Cards") == null || stats.get("Yellow Cards").isNull()
                                ? null : stats.get("Yellow Cards").asInt();

                Integer redCards =
                        stats.get("Red Cards") == null || stats.get("Red Cards").isNull()
                                ? null : stats.get("Red Cards").asInt();

                Integer goalKeepersave =
                        stats.get("Goalkeeper Saves") == null || stats.get("Goalkeeper Saves").isNull()
                                ? null : stats.get("Goalkeeper Saves").asInt();

                Integer totalPasses =
                        stats.get("Total passes") == null || stats.get("Total passes").isNull()
                                ? null : stats.get("Total passes").asInt();

                Integer passesAccurate =
                        stats.get("Passes accurate") == null || stats.get("Passes accurate").isNull()
                                ? null : stats.get("Passes accurate").asInt();


                Integer expectedGoals =
                        stats.get("expected_goals") == null || stats.get("expected_goals").isNull()
                                ? null : stats.get("expected_goals").asInt();

                Integer goalPrevented =
                        stats.get("goals_prevented") == null || stats.get("goals_prevented").isNull()
                                ? null : stats.get("goals_prevented").asInt();



                    MatchStat matchStat = matchStatRepository.findByMatchIdAndClubId_Id(match.getId(),clubs.getId()).orElseGet(MatchStat::new);
                    matchStat.setClubId(clubs);
                    matchStat.setMatch(match);
                    matchStat.setFouls(fouls);
                    matchStat.setShootsOnGoals(apiShootOnGoals);
                    matchStat.setShootOffGoals(apishotsOffGoals);
                    matchStat.setTotalShots(totalShoot);
                    matchStat.setBlockedShots(blockedShoot);
                    matchStat.setShotInsideBox(shotsInsideBox);
                    matchStat.setShotsOutsideBox(shootOutsideBox);
                    matchStat.setCornerKick(cornerKick);
                    matchStat.setOffsides(offsides);
                    matchStat.setBallPossession(ballPossession);
                    matchStat.setYellowCards(yellowCards);
                    matchStat.setRedCards(redCards);
                    matchStat.setGoalkeeperSave(goalKeepersave);
                    matchStat.setTotalPasses(totalPasses);
                    matchStat.setPassesAccurate(passesAccurate);
                    matchStat.setPassesPercentage(passePercentage);
                    matchStat.setExpectedGoals(expectedGoals);
                    matchStat.setGoalsPrevented(goalPrevented);

                    matchStatRepository.save(matchStat);

                }

            }

        }

    private JsonNode callApiWithRetry(String url) throws Exception {

        int maxRetries = 8;
        long baseWaitMs = 150;   // ✅ réduit (600ms c’est énorme)
        long backoffMs = 1200;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("x-apisports-key", apiSportsKey)
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 429) {
                Thread.sleep(backoffMs);
                backoffMs = Math.min(backoffMs * 2, 20000);
                continue;
            }

            if (resp.statusCode() >= 400) {
                throw new RuntimeException("API error " + resp.statusCode() + " body=" + resp.body());
            }

            Thread.sleep(baseWaitMs);
            return objectMapper.readTree(resp.body());
        }

        throw new RateLimitException("Too many 429 retries for url=" + url);
    }


}
