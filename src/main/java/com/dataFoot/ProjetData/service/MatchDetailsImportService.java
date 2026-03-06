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
import java.util.stream.Collectors;

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
     * - forceResync=true : migration (delete + reimport)
     * - forceResync=false: comportement normal (skip finished déjà importé)
     */
    @Transactional
    public String importEventsAndLineupsForLeague(Long leagueId) {

        // ---- Réglages (mets forceResync=true UNE FOIS pour migration) ----
        boolean forceResync = false;      // ✅ passe à false après ton run migration
        int windowDaysBack = 30;         // ✅ évite d'importer toute la saison (mets grand si tu veux tout)
        // -----------------------------------------------------------------

        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(windowDaysBack);

        List<Match> matches = matchRepository.findByLeagueId(leagueId);

        // Cache clubs (1 seule requête)
        Map<Long, Club> clubByApiTeamId = clubRepository.findByLeagueId(leagueId).stream()
                .filter(c -> c.getApiFootballTeamId() != null)
                .collect(Collectors.toMap(Club::getApiFootballTeamId, c -> c));

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

            boolean isFinished = match.isPlayed();
            boolean isToday = match.getMatchDate() != null && match.getMatchDate().isEqual(today);

            boolean fetchEvents = isFinished || isToday;
            boolean fetchLineups = isFinished || isToday;

            if (!fetchEvents && !fetchLineups) {
                matchesSkippedFuture++;
                continue;
            }

            // ---------- EVENTS ----------
            if (fetchEvents) {
                boolean alreadyImported = matchEventRepository.existsByMatchId(match.getId());

                boolean mustResync = forceResync || isToday || !alreadyImported;

                if (isFinished && alreadyImported && !mustResync) {
                    eventsSkippedAlreadyDone++;
                } else {
                    matchEventRepository.deleteAllByMatchId(match.getId());
                    eventsImported += importEventsForMatchOptimized(match, fixtureId, clubByApiTeamId);
                }
            }

            // ---------- LINEUPS ----------
            if (fetchLineups) {
                lineupsUpserted += importLineupsForMatchUpsertOptimized(match, fixtureId, leagueId, clubByApiTeamId);
            }
        }

        return "OK leagueId=" + leagueId
                + " eventsImported=" + eventsImported
                + " eventsSkippedFinishedAlreadyDone=" + eventsSkippedAlreadyDone
                + " lineupsUpserted=" + lineupsUpserted
                + " skippedNoFixture=" + matchesSkippedNoFixture
                + " skippedFuture=" + matchesSkippedFuture
                + " from=" + from + " to=" + today
                + " forceResync=" + forceResync;
    }

    // -------------------- EVENTS (OPTIMIZED) --------------------

    private int importEventsForMatchOptimized(Match match,
                                              int fixtureId,
                                              Map<Long, Club> clubByApiTeamId) {
        try {
            String url = BASE_URL + "/fixtures/events?fixture=" + fixtureId;
            JsonNode response = callApiWithRetry(url).path("response");
            if (!response.isArray()) return 0;

            // 1) Récupére tout les id des joueurs mentionnés dans l'event.
            Set<Integer> apiPlayerIds = new HashSet<>();
            for (JsonNode e : response) {
                Integer p = e.path("player").path("id").isMissingNode() ? null : e.path("player").path("id").asInt();
                Integer a = e.path("assist").path("id").isMissingNode() ? null : e.path("assist").path("id").asInt();
                if (p != null) apiPlayerIds.add(p);
                if (a != null) apiPlayerIds.add(a);
            }

            // 2) Charger tout les joueurs en une seul requete et creer une Map
            Map<Integer, Player> playerByApiId = playerRepository.findByApiFootballPlayerIdIn(apiPlayerIds).stream()
                    .collect(Collectors.toMap(Player::getApiFootballPlayerId, p -> p));

            List<MatchEvent> toSave = new ArrayList<>();

            for (JsonNode e : response) {

                Long apiTeamId = e.path("team").path("id").isMissingNode() ? null : e.path("team").path("id").asLong();
                if (apiTeamId == null) continue;

                Club club = clubByApiTeamId.get(apiTeamId);
                if (club == null) continue;

                int minute = e.path("time").path("elapsed").asInt(0);
                Integer extra = e.path("time").path("extra").isNull() ? null : e.path("time").path("extra").asInt();
                if (extra != null) minute += extra;

                String type = e.path("type").asText("");
                String detail = e.path("detail").asText("");

                EventType eventType = mapEventType(type, detail);
                if (eventType == null) eventType = EventType.OTHER;

                Integer apiPlayerId = e.path("player").path("id").isMissingNode() ? null : e.path("player").path("id").asInt();
                Integer apiAssistId = e.path("assist").path("id").isMissingNode() ? null : e.path("assist").path("id").asInt();

                Player mainPlayer = (apiPlayerId == null) ? null : playerByApiId.get(apiPlayerId);
                Player assistPlayer = (apiAssistId == null) ? null : playerByApiId.get(apiAssistId);

                String playerName = e.path("player").path("name").isMissingNode() ? null : e.path("player").path("name").asText(null);
                String assistName = e.path("assist").path("name").isMissingNode() ? null : e.path("assist").path("name").asText(null);

                MatchEvent me = new MatchEvent();
                me.setMatch(match);
                me.setClub(club);
                me.setMinute(minute);
                me.setEventType(eventType);

                if (eventType == EventType.SUBSTITUTION) {
                    // substitution: player = out, assist = in
                    me.setPlayerOut(mainPlayer);
                    me.setPlayerIn(assistPlayer);

                    // fallback noms (si ids null/non trouvés)
                    me.setPlayerOutName(playerName);
                    me.setPlayerInName(assistName);

                    me.setPlayer(null);
                    me.setAssistPlayer(null);
                    me.setAssistName(null);

                } else {
                    // autres events => joueur principal requis
                    if (mainPlayer == null) continue;

                    me.setPlayer(mainPlayer);

                    boolean isGoal = (eventType == EventType.GOAL || eventType == EventType.PENALTY_GOAL);
                    if (isGoal) {
                        me.setAssistPlayer(assistPlayer);
                        me.setAssistName(assistName);
                    }

                    me.setPlayerOut(null);
                    me.setPlayerIn(null);
                    me.setPlayerOutName(null);
                    me.setPlayerInName(null);
                }

                toSave.add(me);
            }

            matchEventRepository.saveAll(toSave);
            return toSave.size();

        } catch (Exception ex) {
            System.out.println("❌ importEventsForMatch failed matchId=" + match.getId()
                    + " fixtureId=" + fixtureId + " msg=" + ex.getMessage());
            ex.printStackTrace();
            return 0;
        }
    }

    // -------------------- LINEUPS (OPTIMIZED) --------------------

    private int importLineupsForMatchUpsertOptimized(Match match,
                                                     int fixtureId,
                                                     Long leagueId,
                                                     Map<Long, Club> clubByApiTeamId) {
        try {
            String url = BASE_URL + "/fixtures/lineups?fixture=" + fixtureId;
            JsonNode response = callApiWithRetry(url).path("response");
            if (!response.isArray()) return 0;

            // preload existing lineups in 1 query (évite N requêtes)
            Map<Long, MatchLineUp> existingByPlayerId = matchLineUpRepository.findByMatchId(match.getId()).stream()
                    .collect(Collectors.toMap(lu -> lu.getPlayer().getId(), lu -> lu));

            // collect api ids
            Set<Integer> apiPlayerIds = new HashSet<>();
            for (JsonNode teamLineup : response) {
                JsonNode startXI = teamLineup.path("startXI");
                if (startXI.isArray()) {
                    for (JsonNode p : startXI) {
                        Integer id = p.path("player").path("id").isMissingNode() ? null : p.path("player").path("id").asInt();
                        if (id != null) apiPlayerIds.add(id);
                    }
                }
                JsonNode subs = teamLineup.path("substitutes");
                if (subs.isArray()) {
                    for (JsonNode p : subs) {
                        Integer id = p.path("player").path("id").isMissingNode() ? null : p.path("player").path("id").asInt();
                        if (id != null) apiPlayerIds.add(id);
                    }
                }
            }

            Map<Integer, Player> playerByApiId = playerRepository.findByApiFootballPlayerIdIn(apiPlayerIds).stream()
                    .collect(Collectors.toMap(Player::getApiFootballPlayerId, p -> p));

            Set<Long> seenPlayerIds = new HashSet<>();
            List<MatchLineUp> toSave = new ArrayList<>();

            for (JsonNode teamLineup : response) {

                Long apiTeamId = teamLineup.path("team").path("id").isMissingNode() ? null : teamLineup.path("team").path("id").asLong();
                if (apiTeamId == null) continue;

                Club club = clubByApiTeamId.get(apiTeamId);
                if (club == null) continue;

                JsonNode startXI = teamLineup.path("startXI");
                if (startXI.isArray()) {
                    for (JsonNode p : startXI) {
                        upsertLineupFast(match, club, p.path("player"), true, playerByApiId, existingByPlayerId, seenPlayerIds, toSave);
                    }
                }

                JsonNode subs = teamLineup.path("substitutes");
                if (subs.isArray()) {
                    for (JsonNode p : subs) {
                        upsertLineupFast(match, club, p.path("player"), false, playerByApiId, existingByPlayerId, seenPlayerIds, toSave);
                    }
                }
            }

            // save batch
            matchLineUpRepository.saveAll(toSave);

            // delete absent players
            if (!seenPlayerIds.isEmpty()) {
                matchLineUpRepository.deleteByMatchIdAndPlayerIdNotIn(match.getId(), seenPlayerIds);
            }

            return toSave.size();

        } catch (Exception ex) {
            return 0;
        }
    }

    private void upsertLineupFast(Match match,
                                  Club club,
                                  JsonNode playerNode,
                                  boolean starter,
                                  Map<Integer, Player> playerByApiId,
                                  Map<Long, MatchLineUp> existingByPlayerId,
                                  Set<Long> seenPlayerIds,
                                  List<MatchLineUp> toSave) {

        Integer apiPlayerId = playerNode.path("id").isMissingNode() ? null : playerNode.path("id").asInt();
        if (apiPlayerId == null) return;

        Player player = playerByApiId.get(apiPlayerId);
        if (player == null) return;

        String pos = playerNode.path("pos").asText(null);
        Position position = mapPositionFromLineupPos(pos);
        if (position == null) position = player.getPosition();
        if (position == null) return;

        seenPlayerIds.add(player.getId());

        MatchLineUp lu = existingByPlayerId.getOrDefault(player.getId(), new MatchLineUp());
        lu.setMatch(match);
        lu.setClub(club);
        lu.setPlayer(player);
        lu.setStarter(starter);
        lu.setPosition(position);

        toSave.add(lu);
    }

    // -------------------- API CALL --------------------

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