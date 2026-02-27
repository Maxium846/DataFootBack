package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.enumeration.Position;
import com.dataFoot.ProjetData.exception.RateLimitException;
import com.dataFoot.ProjetData.model.*;
import com.dataFoot.ProjetData.repository.*;
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
import java.util.List;

@Service
public class MatchDetailsImportService {

    private static final String BASE_URL = "https://v3.football.api-sports.io";

    private final MatchRepository matchRepository;
    private final ClubRepository clubRepository;
    private final PlayersRepository playerRepository;
    private final MatchLineUpRepository matchLineUpRepository;
    private final MatchEventRepository matchEventRepository;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${apisports.key}")
    private String apiSportsKey;

    // --- Constructor (avec PlayerRepository) ---
    public MatchDetailsImportService(MatchRepository matchRepository,
                                     ClubRepository clubRepository,
                                     PlayersRepository playerRepository,
                                     MatchLineUpRepository matchLineUpRepository,
                                     MatchEventRepository matchEventRepository,
                                     ObjectMapper objectMapper) {
        this.matchRepository = matchRepository;
        this.clubRepository = clubRepository;
        this.playerRepository = playerRepository;
        this.matchLineUpRepository = matchLineUpRepository;
        this.matchEventRepository = matchEventRepository;
        this.objectMapper = objectMapper;
    }

    // --- Import league (inchangé) ---
    @Transactional
    public String importEventsAndLineupsForLeague(Long leagueId) {

        List<Match> matches = matchRepository.findByLeagueId(leagueId);

        int eventsSaved = 0;
        int lineupsSaved = 0;
        int skippedNoFixture = 0;

        LocalDate today = LocalDate.now();   // ✅ ICI (avant la boucle)

        for (Match match : matches) {

            Integer fixtureId = match.getApiFootballFixtureId();
            if (fixtureId == null) {
                skippedNoFixture++;
                continue;
            }

            // ✅ AJOUTE CE BLOC ICI
            boolean fetchEvents  = match.isPlayed();
            boolean fetchLineups = match.isPlayed()
                    || (match.getMatchDate() != null && match.getMatchDate().isEqual(today));

            if (!fetchEvents && !fetchLineups) {
                continue;   // on skip les matchs futurs
            }

            // nettoyage
            matchEventRepository.deleteAllByMatchId(match.getId());
            matchLineUpRepository.deleteAllByMatchId(match.getId());

            if (fetchEvents) {
                eventsSaved += importEventsForMatch(match, fixtureId, leagueId);
            }

            if (fetchLineups) {
                lineupsSaved += importLineupsForMatch(match, fixtureId, leagueId);
            }
        }

        return "OK: events=" + eventsSaved
                + ", lineups=" + lineupsSaved
                + ", matchs sans fixtureId=" + skippedNoFixture;
    }
    // --- Events (avec assists) ---
    private int importEventsForMatch(Match match, int fixtureId, Long leagueId) {
        try {
            String url = BASE_URL + "/fixtures/events?fixture=" + fixtureId;
            JsonNode response = callApiWithRetry(url).path("response");
            if (!response.isArray()) return 0;

            int saved = 0;

            for (JsonNode e : response) {


                // Club
                Long apiTeamId = e.path("team").path("id").isMissingNode() ? null : e.path("team").path("id").asLong();
                if (apiTeamId == null) continue;

                Club club = clubRepository.findByLeagueIdAndApiFootballTeamId(leagueId, apiTeamId).orElse(null);
                if (club == null) continue;

                // Minute
                int minute = e.path("time").path("elapsed").asInt(0);
                Integer extra = e.path("time").path("extra").isNull() ? null : e.path("time").path("extra").asInt();
                if (extra != null) minute = minute + extra;

                String type = e.path("type").asText("");
                String detail = e.path("detail").asText("");

                EventType eventType = mapEventType(type, detail);
                if (eventType == null) eventType = EventType.OTHER; // sécurité

                // Player (acteur principal)
                Integer apiPlayerId = e.path("player").path("id").isMissingNode() ? null : e.path("player").path("id").asInt();
                Player player = null;
                if (apiPlayerId != null) {
                    player = playerRepository.findByApiFootballPlayerId(apiPlayerId).orElse(null);
                }

                // Assist (passeur) seulement pour les buts
                Player assistPlayer = null;
                String assistName = null;

                boolean isGoalEvent = eventType == EventType.GOAL
                        || eventType == EventType.PENALTY_GOAL;

                if (isGoalEvent) {

                    Integer apiAssistId = e.path("assist").path("id").isMissingNode()
                            ? null
                            : e.path("assist").path("id").asInt();

                    assistName = e.path("assist").path("name").isMissingNode()
                            ? null
                            : e.path("assist").path("name").asText(null);

                    if (apiAssistId != null) {
                        assistPlayer = playerRepository
                                .findByApiFootballPlayerId(apiAssistId)
                                .orElse(null);
                    }
                }
                MatchEvent me = new MatchEvent();
                me.setMatch(match);
                me.setClub(club);
                me.setMinute(minute);
                me.setEventType(eventType);
                me.setPlayer(player);

                // ✅ nécessite que tu aies ajouté ces champs dans MatchEvent:
                // private Player assistPlayer;  (ManyToOne nullable)
                // private String assistName;    (nullable)
                me.setAssistPlayer(assistPlayer);
                me.setAssistName(assistName);

                matchEventRepository.save(me);
                saved++;
            }

            return saved;

        } catch (Exception ex) {
            System.out.println("❌ importEventsForMatch failed: matchId=" + match.getId()
                    + " fixtureId=" + fixtureId + " leagueId=" + leagueId
                    + " msg=" + ex.getMessage());
            ex.printStackTrace();
            return 0;
        }
    }

    // --- Lineups (inchangé) ---
    private int importLineupsForMatch(Match match, int fixtureId, Long leagueId) {
        try {
            String url = BASE_URL + "/fixtures/lineups?fixture=" + fixtureId;
            JsonNode response = callApiWithRetry(url).path("response");
            if (!response.isArray()) return 0;

            int saved = 0;

            for (JsonNode teamLineup : response) {
                Long apiTeamId = teamLineup.path("team").path("id").isMissingNode() ? null : teamLineup.path("team").path("id").asLong();
                if (apiTeamId == null) continue;

                Club club = clubRepository.findByLeagueIdAndApiFootballTeamId(leagueId, apiTeamId).orElse(null);
                if (club == null) continue;

                JsonNode startXI = teamLineup.path("startXI");
                if (startXI.isArray()) {
                    for (JsonNode p : startXI) {
                        saved += saveLineupPlayer(match, club, p.path("player"), true);
                    }
                }

                JsonNode subs = teamLineup.path("substitutes");
                if (subs.isArray()) {
                    for (JsonNode p : subs) {
                        saved += saveLineupPlayer(match, club, p.path("player"), false);
                    }
                }
            }

            return saved;

        } catch (Exception ex) {
            return 0;
        }
    }

    private int saveLineupPlayer(Match match, Club club, JsonNode playerNode, boolean starter) {
        Integer apiPlayerId = playerNode.path("id").isMissingNode() ? null : playerNode.path("id").asInt();
        if (apiPlayerId == null) return 0;

        Player player = playerRepository.findByApiFootballPlayerId(apiPlayerId).orElse(null);
        if (player == null) return 0;

        String pos = playerNode.path("pos").asText(null);
        Position position = mapPositionFromLineupPos(pos);

        if (position == null) position = player.getPosition();
        if (position == null) return 0;

        MatchLineUp lu = new MatchLineUp();
        lu.setMatch(match);
        lu.setClub(club);
        lu.setPlayer(player);
        lu.setStarter(starter);
        lu.setPosition(position);

        matchLineUpRepository.save(lu);
        return 1;
    }

    private JsonNode callApiWithRetry(String url) throws Exception {

        int maxRetries = 8;
        long baseWaitMs = 600;     // throttle de base
        long backoffMs  = 1200;    // backoff si 429


        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("x-apisports-key", apiSportsKey)
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 429) {
                // Attendre + backoff
                Thread.sleep(backoffMs);
                backoffMs = Math.min(backoffMs * 2, 20000);
                continue;
            }

            if (resp.statusCode() >= 400) {
                throw new RuntimeException("API error " + resp.statusCode() + " body=" + resp.body());
            }

            // petite pause entre appels même si OK
            Thread.sleep(baseWaitMs);

            return objectMapper.readTree(resp.body());
        }

        throw new RateLimitException("Too many 429 retries for url=" + url);
    }

    private Position mapPositionFromLineupPos(String pos) {
        if (pos == null) return null;
        return switch (pos) {
            case "G", "Goalkeeper" -> Position.GK;
            case "D", "Defender" -> Position.DEF;
            case "M", "Midfielder" -> Position.MID;
            case "F", "Attacker" -> Position.FWD;
            default -> null;
        };
    }

    private EventType mapEventType(String type, String detail) {
        String t = (type == null) ? "" : type.toLowerCase();
        String d = (detail == null) ? "" : detail.toLowerCase();

        if (t.contains("goal")) {
            // API-FOOTBALL: detail peut être "Normal Goal", "Penalty", "Own Goal"
            if (d.contains("penalty")) return EventType.PENALTY_GOAL;
            if (d.contains("own")) return EventType.OWN_GOAL;
            return EventType.GOAL;
        }

        if (t.contains("card")) {
            if (d.contains("red")) return EventType.RED_CARD;
            return EventType.YELLOW_CARD;
        }

        if (t.contains("subst")) return EventType.SUBSTITUTION;
        if (t.contains("var")) return EventType.VAR;

        return EventType.OTHER;
    }
}
