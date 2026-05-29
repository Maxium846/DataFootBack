package com.dataFoot.match;

import com.dataFoot.league.League;
import com.dataFoot.league.LeagueRepository;
import com.dataFoot.ranking.Ranking;
import com.dataFoot.ranking.RankingRepository;
import com.dataFoot.ranking.RankingService;
import com.dataFoot.team.TeamRepository;
import com.dataFoot.team.Teams;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class FixtureImportService {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final RankingRepository rankingRepository;
    private final RankingService rankingService;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${apisports.key}")
    private String apiSportsKey;

    private static final String BASE_URL = "https://v3.football.api-sports.io";

    public FixtureImportService(LeagueRepository leagueRepository,
                                TeamRepository teamRepository,
                                MatchRepository matchRepository,
                                RankingRepository rankingRepository,
                                RankingService rankingService,
                                ObjectMapper objectMapper) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.rankingRepository = rankingRepository;
        this.rankingService = rankingService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public String generateCalendarFromApiFootball(Long leagueId) {

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        Integer apiFootballLeagueId = league.getApiFootballLeagueId();
        if (apiFootballLeagueId == null) {
            throw new RuntimeException("League missing apiFootballLeagueId (ex Premier League = 39)");
        }


        // Clubs + init classement
        List<Teams> clubs = teamRepository.findByLeagueId(leagueId);
        initClassement(league,clubs);
        if (clubs.size() < 2) throw new RuntimeException("Pas assez de clubs");

        int season = 2025; // 2025/2026
        List<JsonNode> fixtures = fetchFixtures(apiFootballLeagueId, season);

        int created = 0;
        int skipped = 0;

        for (JsonNode item : fixtures) {

            JsonNode fixture = item.path("fixture");
            JsonNode teams = item.path("teams");
            JsonNode goals = item.path("goals");
            JsonNode leagueNode = item.path("league");

            // FixtureId
            int fixtureId = fixture.path("id").asInt();

            // Date (API gives ISO with offset)
            String isoDate = fixture.path("date").asText(null);
            LocalDate matchDate = parseToParisLocalDate(isoDate);
            if (matchDate == null) {
                skipped++;
                continue;
            }

            // Team ids
            Long homeTeamId = teams.path("home").path("id").isMissingNode() ? null : teams.path("home").path("id").asLong();
            Long awayTeamId = teams.path("away").path("id").isMissingNode() ? null : teams.path("away").path("id").asLong();
            if (homeTeamId == null || awayTeamId == null) {
                skipped++;
                continue;
            }

            Teams home = teamRepository.findByLeagueIdAndApiFootballTeamId(leagueId, homeTeamId).orElse(null);
            Teams away = teamRepository.findByLeagueIdAndApiFootballTeamId(leagueId, awayTeamId).orElse(null);
            if (home == null || away == null) {
                // si tes clubs ne sont pas importés / apiFootballTeamId pas renseigné
                skipped++;
                continue;
            }

            Integer journee = parseRoundToJournee(leagueNode.path("round").asText(null));
            if (journee == null) {
                skipped++;
                continue;
            }

            Integer homeGoals = goals.path("home").isNull() ? null : goals.path("home").asInt();
            Integer awayGoals = goals.path("away").isNull() ? null : goals.path("away").asInt();

            String statusShort = fixture.path("status").path("short").asText("");
            boolean played = isPlayed(statusShort) && homeGoals != null && awayGoals != null;

            Match match = matchRepository.findByApiFootballFixtureId(fixtureId)
                    .orElseGet(Match::new);
            match.setLeague(league);
            match.setHomeTeams(home);
            match.setAwayTeams(away);
            match.setJournee(journee);
            match.setMatchDate(matchDate);

            match.setHomeGoals(homeGoals);
            match.setAwayGoals(awayGoals);
            match.setPlayed(played);

            match.setApiFootballFixtureId(fixtureId);

            matchRepository.save(match);
            created++;
        }

        rankingService.recalculateLeague(league);

        return created + " matchs importés, " + skipped + " ignorés, classement recalculé.";
    }

    private void initClassement(League league, List<Teams> t) {
        List<Ranking> rankings = new ArrayList<>();
        for (Teams teams : t) {
            Ranking c = new Ranking();
            c.setLeague(league);
            c.setTeams(teams);
            c.setPoints(0);
            c.setPlayed(0);
            c.setWins(0);
            c.setDraws(0);
            c.setLosses(0);
            c.setGoalsFor(0);
            c.setGoalsAgainst(0);
            c.setGoalDifference(0);
            rankings.add(c);
        }
        rankingRepository.saveAll(rankings);
    }

    private List<JsonNode> fetchFixtures(int apiLeagueId, int season) {
        try {
            String url = BASE_URL + "/fixtures?league=" + apiLeagueId + "&season=" + season;
            String json = fetchUrlWithHeaders(url);

            JsonNode root = objectMapper.readTree(json);
            JsonNode response = root.path("response");
            if (!response.isArray()) return List.of();

            List<JsonNode> out = new ArrayList<>();
            response.forEach(out::add);
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Erreur import fixtures API-FOOTBALL", e);
        }
    }

    private String fetchUrlWithHeaders(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("x-apisports-key", apiSportsKey)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("API error " + resp.statusCode() + " body=" + resp.body());
        }
        return resp.body();
    }

    private boolean isPlayed(String statusShort) {
        return "FT".equals(statusShort) || "AET".equals(statusShort) || "PEN".equals(statusShort);
    }

    private Integer parseRoundToJournee(String round) {
        if (round == null || round.isBlank()) return null;
        // "Regular Season - 1"
        int idx = round.lastIndexOf('-');
        if (idx < 0) return null;
        String n = round.substring(idx + 1).trim();
        try {
            return Integer.parseInt(n);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseToParisLocalDate(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) return null;
        try {
            OffsetDateTime odt = OffsetDateTime.parse(isoDate);
            return odt.atZoneSameInstant(ZoneId.of("Europe/Paris")).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }
}