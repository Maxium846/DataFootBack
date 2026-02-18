package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.dto.club.ClubDtoFpl;
import com.dataFoot.ProjetData.mapper.ClubMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.repository.ClubRepositoryInterface;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubService {

    private final ClubRepositoryInterface clubRepositoryInterface;
    private final LeagueRepositoryInterface leagueRepositoryInterface;
    private final ObjectMapper objectMapper;



    public ClubService(ClubRepositoryInterface clubRepositoryInterface, LeagueRepositoryInterface leagueRepositoryInterface, ObjectMapper objectMapper) {
        this.clubRepositoryInterface = clubRepositoryInterface;
        this.leagueRepositoryInterface = leagueRepositoryInterface;
        this.objectMapper = objectMapper;
    }

    public ClubDto createClub(ClubDto dto) {

        League league = leagueRepositoryInterface.findById(dto.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found"));


        //« À partir des données du DTO et de la ligue trouvée en base,
        //crée-moi un objet Club prêt à être sauvegardé.
        Club club = ClubMapper.toEntity(dto, league);

        Club saved = clubRepositoryInterface.save(club);

        leagueRepositoryInterface.findById(dto.getLeagueId()).orElseThrow(() -> new RuntimeException("League not found"));


        return ClubMapper.toDto(saved);
    }


    public List<ClubDto> findAll() {
        return clubRepositoryInterface.findAll()
                .stream()
                .map(ClubMapper::toDto)
                .toList();
    }

    public ClubDto findById(Long id) {
        Club club = clubRepositoryInterface.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        return ClubMapper.toDto(club);
    }

    public ClubDto update(Long id, ClubDto dto) {
        Club club = clubRepositoryInterface.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        ClubMapper.updateEntity(club, dto);
        Club updated = clubRepositoryInterface.save(club);

        return ClubMapper.toDto(updated);
    }
    public void delete(Long id) {
        if (!clubRepositoryInterface.existsById(id)) {
            throw new RuntimeException("Club not found");
        }
        clubRepositoryInterface.deleteById(id);
    }

    public List<ClubDtoFpl> generateOrUpdateClubs(Long leagueId) {
        List<ClubDtoFpl> result = new ArrayList<>();

        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        try {
            // 1️⃣ Récupérer le JSON complet depuis FPL
            String bootstrapJson = fetchUrl("https://fantasy.premierleague.com/api/bootstrap-static/");

            // 2️⃣ Parser JSON
            JsonNode root = objectMapper.readTree(bootstrapJson);
            JsonNode teams = root.get("teams");

            for (JsonNode teamNode : teams) {
                Integer fplId = teamNode.get("id").asInt();
                String name = teamNode.get("name").asText();

                // Vérifie si le club existe
                Club club = clubRepositoryInterface.findByFplId(fplId)
                        .orElseGet(() -> new Club());

                club.setFplId(fplId);
                club.setName(name);
                club.setLeague(league);

                // Sauvegarde en DB
                Club savedClub = clubRepositoryInterface.save(club);

                // Ajoute au DTO
                result.add(new ClubDtoFpl(
                        savedClub.getId(),
                        savedClub.getName(),
                        savedClub.getFplId(),
                        savedClub.getLeague() != null ? savedClub.getLeague().getId() : null));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'import des clubs : " + e.getMessage(), e);
        }

        return result;
    }


    /**
             * Méthode utilitaire pour récupérer le JSON depuis une URL
             */
            private String fetchUrl(String urlString) throws IOException {
                URL url = new URL(urlString);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    return reader.lines().collect(Collectors.joining());
                }
            }
        }







