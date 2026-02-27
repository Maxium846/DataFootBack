package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.dto.fpl.ClubDtoFpl;
import com.dataFoot.ProjetData.mapper.ClubMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.repository.ClubRepository;
import com.dataFoot.ProjetData.repository.LeagueRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubService {

    private final ClubRepository clubRepository;
    private final LeagueRepository leagueRepository;
    private final ObjectMapper objectMapper;



    @Value("${apisports.key}")
    private String apiKey;

    public ClubService(ClubRepository clubRepository, LeagueRepository leagueRepository, ObjectMapper objectMapper) {
        this.clubRepository = clubRepository;
        this.leagueRepository = leagueRepository;
        this.objectMapper = objectMapper;
    }

    public ClubDto createClub(ClubDto dto) {

        League league = leagueRepository.findById(dto.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found"));


        //« À partir des données du DTO et de la ligue trouvée en base,
        //crée-moi un objet Club prêt à être sauvegardé.
        Club club = ClubMapper.toEntity(dto, league);

        Club saved = clubRepository.save(club);

        leagueRepository.findById(dto.getLeagueId()).orElseThrow(() -> new RuntimeException("League not found"));


        return ClubMapper.toDto(saved);
    }


    public List<ClubDto> findAll() {
        return clubRepository.findAll()
                .stream()
                .map(ClubMapper::toDto)
                .toList();
    }

    public ClubDto findById(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        return ClubMapper.toDto(club);
    }

    public ClubDto update(Long id, ClubDto dto) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        ClubMapper.updateEntity(club, dto);
        Club updated = clubRepository.save(club);

        return ClubMapper.toDto(updated);
    }
    public void delete(Long id) {
        if (!clubRepository.existsById(id)) {
            throw new RuntimeException("Club not found");
        }
        clubRepository.deleteById(id);
    }


    public List<ClubDtoFpl> generateOrUpdateClubsApiFootball(Long leagueId, int season) {
        List<ClubDtoFpl> result = new ArrayList<>();

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        try {
            // 1) Appel API-FOOTBALL (avec header x-apisports-key)
            String json = fetchApiSports(
                    "https://v3.football.api-sports.io/teams?league=39&season=" + season
            );

            // 2) Parser JSON
            JsonNode root = objectMapper.readTree(json);
            JsonNode response = root.get("response");

            if (response == null || !response.isArray()) {
                throw new RuntimeException("Réponse API inattendue: 'response' absent");
            }

            for (JsonNode item : response) {
                JsonNode teamNode = item.get("team");
                if (teamNode == null) continue;

                long apiTeamId = teamNode.get("id").asLong();
                String name = teamNode.get("name").asText();
                // Upsert club
                Club club = clubRepository.findByApiFootballTeamId(apiTeamId)
                        .orElseGet(Club::new);

                club.setApiFootballTeamId(apiTeamId);
                club.setName(name);
                club.setLeague(league);

                Club saved = clubRepository.save(club);

                result.add(new ClubDtoFpl(
                        saved.getId(),
                        saved.getName(),
                        saved.getLeague() != null ? saved.getLeague().getId() : null
                ));
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Erreur import clubs API-FOOTBALL : " + e.getMessage(), e);
        }
    }

    /**
     * Appel HTTP avec header x-apisports-key
     */
    private String fetchApiSports(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("x-apisports-key", apiKey); // <- ta clé (injectée)
        con.setConnectTimeout(15000);
        con.setReadTimeout(30000);

        int status = con.getResponseCode();
        InputStream is = (status >= 200 && status < 300) ? con.getInputStream() : con.getErrorStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String body = reader.lines().collect(Collectors.joining());
            if (status < 200 || status >= 300) {
                throw new RuntimeException("HTTP " + status + " - " + body);
            }
            return body;
        } finally {
            con.disconnect();
        }
    }
        }








