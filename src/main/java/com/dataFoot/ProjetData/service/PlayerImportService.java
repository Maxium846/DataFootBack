package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.player.PlayerApiDto;
import com.dataFoot.ProjetData.enumeration.Position;
import com.dataFoot.ProjetData.exception.RateLimitException;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.ClubRepository;
import com.dataFoot.ProjetData.repository.LeagueRepository;
import com.dataFoot.ProjetData.repository.PlayersRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerImportService {

    private final LeagueRepository leagueRepository;
    private final ClubRepository clubRepository;
    private final PlayersRepository playerRepository;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${apisports.key}")
    private String apiSportsKey;

    private static final String BASE_URL = "https://v3.football.api-sports.io";

    public PlayerImportService(LeagueRepository leagueRepository,
                               ClubRepository clubRepository,
                               PlayersRepository playerRepository, ObjectMapper objectMapper) {
        this.leagueRepository = leagueRepository;
        this.clubRepository = clubRepository;
        this.playerRepository = playerRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Void generateOrUpdatePlayers(Long leagueId) throws Exception {

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        Integer apiFootballLeagueId = league.getApiFootballLeague();
        if (apiFootballLeagueId == null) {
            throw new RuntimeException("League missing apiFootballLeagueId (ex: Premier League = 39)");
        }

        List<Club> clubs = clubRepository.findByLeagueId(leagueId);

        for (Club club : clubs) {

            Long apiFootballClubId = club.getApiFootballTeamId();
            String url = BASE_URL + "/players/squads?team=" + apiFootballClubId;
            JsonNode response = callApiWithRetry(url).path("response");
            if (response == null || !response.isArray()) break;

            for (JsonNode item : response) {

                JsonNode playerNode = item.path("players");
                if (playerNode.isMissingNode() || playerNode.isNull()) continue;

                for (JsonNode jsonNode : playerNode) {
                    int apiPlayerId = jsonNode.path("id").asInt();
                    String firstName = text(jsonNode, "name");
                    String apiPosition = text(jsonNode, "position");
                    Position position = mapPositionFromApiFootball(apiPosition);
                    int number = jsonNode.path("number").asInt();

                    Player player = playerRepository.findByApiFootballPlayerId(apiPlayerId)
                            .orElseGet(Player::new);

                    LocalDate birthDate = player.getDateDeNaissance();
                    String nation = player.getNation();
                    String taille = player.getTaille();
                    String poids = player.getPoids();
                    String photo = player.getPhoto();

                    boolean needProfile =
                            birthDate == null ||
                                    nation == null ||
                                    nation.isBlank() ||
                                    taille == null ||
                                    taille.isBlank() ||
                                    poids == null ||
                                    poids.isBlank()||
                                    photo == null ||
                                    photo.isBlank();



                    if (needProfile) {
                        String urlPlayer = BASE_URL + "/players/profiles?player=" + apiPlayerId;
                        JsonNode responsePlayer = callApiWithRetry(urlPlayer).path("response");

                        if (responsePlayer != null && responsePlayer.isArray() && !responsePlayer.isEmpty()) {
                            JsonNode itemPlayer = responsePlayer.get(0);
                            JsonNode playerNodes = itemPlayer.path("player");

                            if (!playerNodes.isMissingNode() && !playerNodes.isNull()) {
                                JsonNode birthDateNode = playerNodes.path("birth").path("date");
                                if (!birthDateNode.isMissingNode()
                                        && !birthDateNode.isNull()
                                        && !birthDateNode.asText().isBlank()) {
                                    birthDate = LocalDate.parse(birthDateNode.asText());
                                }

                                nation = playerNodes.path("nationality").asText(null);
                                taille = playerNodes.path("height").asText(null);
                                poids = playerNodes.path("weight").asText(null);
                                photo=playerNodes.path("photo").asText(null);
                            }
                        }
                    }

                    player.setApiFootballPlayerId(apiPlayerId);
                    player.setFirstName(firstName);
                    player.setPosition(position);
                    player.setNumber(number);
                    player.setClub(club);
                    player.setDateDeNaissance(birthDate);
                    player.setNation(nation);
                    player.setPoids(poids);
                    player.setTaille(taille);
                    player.setPhoto(photo);

                    playerRepository.save(player);
                }

            }
        }
        return null;

    }



    private JsonNode callApiWithRetry(String url) throws Exception {

        int maxRetries = 8;
        long baseWaitMs = 150;
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

    private String fetchUrlWithHeaders(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("x-apisports-key", apiSportsKey)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new IOException("API error " + resp.statusCode() + " body=" + resp.body());
        }
        return resp.body();
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    private Position mapPositionFromApiFootball(String apiPos) {
        if (apiPos == null) return null;
        return switch (apiPos) {
            case "Goalkeeper" -> Position.GK;
            case "Defender" -> Position.DEF;
            case "Midfielder" -> Position.MID;
            case "Attacker" -> Position.FWD;
            default -> null;
        };
    }
}
