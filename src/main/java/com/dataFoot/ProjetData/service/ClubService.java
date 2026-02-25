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

    private final ClubRepository clubRepository;
    private final LeagueRepository leagueRepository;
    private final ObjectMapper objectMapper;



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

    public List<ClubDtoFpl> generateOrUpdateClubs(Long leagueId) {
        List<ClubDtoFpl> result = new ArrayList<>();

        League league = leagueRepository.findById(leagueId)
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
                Club club = clubRepository.findByFplId(fplId)
                        .orElseGet(() -> new Club());

                club.setFplId(fplId);
                club.setName(name);
                club.setLeague(league);

                // Sauvegarde en DB
                Club savedClub = clubRepository.save(club);

                // Ajoute au DTO
                result.add(new ClubDtoFpl(
                        savedClub.getId(),
                        savedClub.getName(),
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







