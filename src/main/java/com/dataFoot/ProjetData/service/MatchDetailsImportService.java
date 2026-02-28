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
import java.util.*;

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

    /**
     * Stratégie:
     * - Events:
     *   - si match FINI: import 1 fois, puis on skip (si existe déjà)
     *   - si match du jour (LIVE possible): on peut resync (delete+insert)
     * - Lineups:
     *   - upsert par (matchId, playerId)
     *   - puis delete des joueurs absents de la réponse
     */
    @Transactional
    public String importEventsAndLineupsForLeague(Long leagueId) {

        List<Match> matches = matchRepository.findByLeagueId(leagueId);
        LocalDate today = LocalDate.now();

        int eventsImported = 0;
        int eventsSkippedAlreadyDone = 0;
        int lineupsUpserted = 0;
        int matchesSkippedNoFixture = 0;
        int matchesSkippedFuture = 0;

        for (Match match : matches) {

            Integer fixtureId = match.getApiFootballFixtureId();
            if (fixtureId == null) {
                matchesSkippedNoFixture++;
                continue;
            }

            boolean isFinished = match.isPlayed(); // chez toi: played = match terminé
            boolean isToday = match.getMatchDate() != null && match.getMatchDate().isEqual(today);

            // Règles de fetch
            boolean fetchEvents = isFinished || isToday;   // events: fini ou match du jour
            boolean fetchLineups = isFinished || isToday;  // lineups: fini ou match du jour

            // si match futur (pas aujourd'hui et pas fini), on skip
            if (!fetchEvents && !fetchLineups) {
                matchesSkippedFuture++;
                continue;
            }

            // 1) EVENTS
            if (fetchEvents) {
                boolean alreadyImported = matchEventRepository.existsByMatchId(match.getId());

                if (isFinished && alreadyImported) {
                    // Match fini et events déjà importés -> on ne touche plus
                    eventsSkippedAlreadyDone++;
                } else {
                    // Match du jour (LIVE) ou match fini jamais importé -> resync events
                    matchEventRepository.deleteAllByMatchId(match.getId());
                    eventsImported += importEventsForMatch(match, fixtureId, leagueId);
                }
            }

            // 2) LINEUPS
            if (fetchLineups) {
                lineupsUpserted += importLineupsForMatchUpsert(match, fixtureId, leagueId);
            }
        }

        return "OK leagueId=" + leagueId
                + " eventsImported=" + eventsImported
                + " eventsSkippedFinishedAlreadyDone=" + eventsSkippedAlreadyDone
                + " lineupsUpserted=" + lineupsUpserted
                + " skippedNoFixture=" + matchesSkippedNoFixture
                + " skippedFuture=" + matchesSkippedFuture;
    }

    // -------------------- EVENTS --------------------

    private int importEventsForMatch(Match match, int fixtureId, Long leagueId) {
        try {
            String url = BASE_URL + "/fixtures/events?fixture=" + fixtureId;
            JsonNode response = callApiWithRetry(url).path("response");
            if (!response.isArray()) return 0;

            int saved = 0;

            for (JsonNode e : response) {

                Long apiTeamId = e.path("team").path("id").isMissingNode() ? null : e.path("team").path("id").asLong();
                if (apiTeamId == null) continue;

                Club club = clubRepository.findByLeagueIdAndApiFootballTeamId(leagueId, apiTeamId).orElse(null);
                if (club == null) continue;

                // Minute + extra
                int minute = e.path("time").path("elapsed").asInt(0);
                Integer extra = e.path("time").path("extra").isNull() ? null : e.path("time").path("extra").asInt();
                if (extra != null) minute += extra;

                String type = e.path("type").asText("");
                String detail = e.path("detail").asText("");

                EventType eventType = mapEventType(type, detail);
                if (eventType == null) eventType = EventType.OTHER;

                Integer apiPlayerId = e.path("player").path("id").isMissingNode() ? null : e.path("player").path("id").asInt();
                Player player = null;
                if (apiPlayerId != null) {
                    player = playerRepository.findByApiFootballPlayerId(apiPlayerId).orElse(null);
                }
                // si pas de player en base -> on skip (sinon tu auras des events “orphelins”)
                if (player == null) continue;

                // Assist uniquement pour buts
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
                        assistPlayer = playerRepository.findByApiFootballPlayerId(apiAssistId).orElse(null);
                    }
                }

                // IMPORTANT: pas d'UPSERT naïf par playerId -> un joueur peut avoir plusieurs events.
                MatchEvent me = new MatchEvent();
                me.setMatch(match);
                me.setClub(club);
                me.setMinute(minute);
                me.setEventType(eventType);
                me.setPlayer(player);
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

    // -------------------- LINEUPS (UPSERT + DELETE ABSENTS) --------------------

    private int importLineupsForMatchUpsert(Match match, int fixtureId, Long leagueId) {
        try {
            String url = BASE_URL + "/fixtures/lineups?fixture=" + fixtureId;
            JsonNode response = callApiWithRetry(url).path("response");
            if (!response.isArray()) return 0;

            int upserted = 0;
            Set<Long> seenPlayerIds = new HashSet<>();

            for (JsonNode teamLineup : response) {

                Long apiTeamId = teamLineup.path("team").path("id").isMissingNode()
                        ? null
                        : teamLineup.path("team").path("id").asLong();
                if (apiTeamId == null) continue;

                Club club = clubRepository.findByLeagueIdAndApiFootballTeamId(leagueId, apiTeamId).orElse(null);
                if (club == null) continue;

                JsonNode startXI = teamLineup.path("startXI");
                if (startXI.isArray()) {
                    for (JsonNode p : startXI) {
                        upserted += upsertLineupPlayer(match, club, p.path("player"), true, seenPlayerIds);
                    }
                }

                JsonNode subs = teamLineup.path("substitutes");
                if (subs.isArray()) {
                    for (JsonNode p : subs) {
                        upserted += upsertLineupPlayer(match, club, p.path("player"), false, seenPlayerIds);
                    }
                }
            }

            // Nettoyage: supprimer les joueurs qui ne sont plus dans la réponse
            // (Si la feuille n'est pas dispo, seenPlayerIds sera vide -> on ne delete rien)
            if (!seenPlayerIds.isEmpty()) {
                matchLineUpRepository.deleteByMatchIdAndPlayerIdNotIn(match.getId(), seenPlayerIds);
            }

            return upserted;

        } catch (Exception ex) {
            return 0;
        }
    }

    private int upsertLineupPlayer(Match match,
                                   Club club,
                                   JsonNode playerNode,
                                   boolean starter,
                                   Set<Long> seenPlayerIds) {

        Integer apiPlayerId = playerNode.path("id").isMissingNode() ? null : playerNode.path("id").asInt();
        if (apiPlayerId == null) return 0;

        Player player = playerRepository.findByApiFootballPlayerId(apiPlayerId).orElse(null);
        if (player == null) return 0;

        String pos = playerNode.path("pos").asText(null);
        Position position = mapPositionFromLineupPos(pos);
        if (position == null) position = player.getPosition();
        if (position == null) return 0;

        seenPlayerIds.add(player.getId());

        MatchLineUp lu = matchLineUpRepository
                .findByMatchIdAndPlayerId(match.getId(), player.getId())
                .orElseGet(MatchLineUp::new);

        lu.setMatch(match);
        lu.setClub(club);
        lu.setPlayer(player);
        lu.setStarter(starter);
        lu.setPosition(position);

        matchLineUpRepository.save(lu);
        return 1;
    }

    // -------------------- API CALL --------------------

    private JsonNode callApiWithRetry(String url) throws Exception {

        int maxRetries = 8;
        long baseWaitMs = 600;
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

    // -------------------- MAPPERS --------------------

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