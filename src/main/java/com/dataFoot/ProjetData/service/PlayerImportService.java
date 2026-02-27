package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.player.PlayerFplDto;
import com.dataFoot.ProjetData.enumeration.Position;
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
    public List<PlayerFplDto> generateOrUpdatePlayers(Long leagueId) {

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        Integer apiFootballLeagueId = league.getApiFootballLeague(); // <-- à avoir dans League
        if (apiFootballLeagueId == null) {
            throw new RuntimeException("League missing apiFootballLeagueId (ex: Premier League = 39)");
        }

        int season = 2025; // 2025/2026 = année de début
        List<PlayerFplDto> result = new ArrayList<>();

        try {
            int page = 1;
            int totalPages = 1;

            while (page <= totalPages) {
                String url = BASE_URL + "/players?league=" + apiFootballLeagueId
                        + "&season=" + season
                        + "&page=" + page;

                String json = fetchUrlWithHeaders(url);
                JsonNode root = objectMapper.readTree(json);

                totalPages = root.path("paging").path("total").asInt(1);

                JsonNode response = root.get("response");
                if (response == null || !response.isArray()) break;

                for (JsonNode item : response) {

                    JsonNode playerNode = item.path("player");
                    JsonNode statistics = item.path("statistics");

                    if (playerNode.isMissingNode() || playerNode.isNull()) continue;

                    int apiPlayerId = playerNode.path("id").asInt();
                    String firstName = text(playerNode, "firstname");
                    String lastName = text(playerNode, "lastname");
                    String displayName = text(playerNode, "name");
                    String nationality = text(playerNode, "nationality");

                    // birth.date peut être null
                    LocalDate birthDate = null;
                    JsonNode birth = playerNode.path("birth");
                    if (!birth.isMissingNode() && birth.hasNonNull("date")) {
                        birthDate = LocalDate.parse(birth.get("date").asText());
                    }

                    // --- Choix d'une "stat principale" ---
                    // Souvent statistics[0] suffit. Si tu veux être plus robuste, tu peux chercher celle
                    // dont league.id == apiFootballLeagueId, puis team.id non null.
                    JsonNode mainStat = (statistics.isArray() && statistics.size() > 0)
                            ? statistics.get(0)
                            : null;

                    Long apiTeamId = null;
                    Position position = null;

                    if (mainStat != null) {
                        if (!mainStat.path("team").path("id").isMissingNode()) {
                            apiTeamId = mainStat.path("team").path("id").asLong();
                        }
                        String apiPos = mainStat.path("games").path("position").asText(null);
                        position = mapPositionFromApiFootball(apiPos);
                    }

                    // --- find or create player ---
                    Player player = playerRepository.findByApiFootballPlayerId(apiPlayerId)
                            .orElseGet(Player::new);

                    player.setApiFootballPlayerId(apiPlayerId);
                    player.setFirstName(firstName != null ? firstName : displayName);
                    player.setLastName(lastName);
                    player.setNation(nationality);
                    player.setDateDeNaissance(birthDate);
                    player.setPosition(position);

                    // --- rattacher club via apiFootballTeamId ---
                    if (apiTeamId != null) {
                        Club club = clubRepository
                                .findByLeagueIdAndApiFootballTeamId(leagueId, apiTeamId)
                                .orElse(null);
                        player.setClub(club);
                    } else {
                        player.setClub(null);
                    }

                    Player saved = playerRepository.save(player);

                    result.add(new PlayerFplDto(
                            saved.getId(),
                            saved.getClub() != null ? saved.getClub().getId() : null,
                            saved.getFirstName(),
                            saved.getLastName(),
                            saved.getPosition(),
                            saved.getDateDeNaissance(),
                            saved.getNation(),
                            saved.getApiFootballPlayerId()
                    ));
                }

                page++;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur import joueurs API-FOOTBALL", e);
        }

        return result;
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
