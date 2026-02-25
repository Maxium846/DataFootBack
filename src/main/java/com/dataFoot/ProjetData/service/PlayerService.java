package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerFplDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.mapper.PlayerMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.ClubRepository;
import com.dataFoot.ProjetData.repository.LeagueRepository;
import com.dataFoot.ProjetData.repository.PlayersRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayersRepository playerRepository;
    private final ClubRepository clubRepository;

    private final LeagueRepository leagueRepository;
    private final ObjectMapper objectMapper;


    public PlayerService(PlayersRepository playerRepository, ClubRepository clubRepository, LeagueRepository leagueRepository, ObjectMapper objectMapper, ClubRepository clubRepositoryInterface) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;
        this.leagueRepository = leagueRepository;
        this.objectMapper = objectMapper;
    }

    public Player createPlayer(PlayerDto dto) {
        // 1️⃣ Crée un nouvel objet joueur
        Player player = new Player();
        player.setFirstName(dto.getFirstName());
        player.setLastName(dto.getLastName());
        player.setPosition(dto.getPosition());
        player.setDateDeNaissance(dto.getDateDeNaissance());
        player.setNation(dto.getNation());


        // 2️⃣ Récupère le club correspondant à l'ID fourni
        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        // 3️⃣ Lie le joueur à ce club
        player.setClub(club);

        // 4️⃣ Sauvegarde le joueur dans la base
        return playerRepository.save(player);
    }

    public List<PlayerInClubDto> allPlayer (Long clubId){

        Optional<Club> club = clubRepository.findById(clubId);

        return playerRepository.findByClubId(club.orElseThrow().getId())
                .stream()
                .map(PlayerMapper::toInClubDto)
                .toList();
    }

    public PlayerInClubDto getPlayerById (long id){

        Player player = playerRepository.findById(id).orElseThrow(()-> new RuntimeException("lid du joueur n'esiste pas "));

        return PlayerMapper.toInClubDto(player);

    }

    public void deletePlayer(Long id){

        if(!playerRepository.existsById(id)) {
            throw (new RuntimeException("l'id n'existe pas "));
        }
            playerRepository.deleteById(id);
    }
@Transactional
    public List<PlayerFplDto> generateOrUpdatePlayers(Long leagueId) {

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        List<PlayerFplDto> result = new ArrayList<>();

        playerRepository.deleteByClub_League_Id(league.getId());




        try {

            String bootstrapJson = fetchUrl("https://fantasy.premierleague.com/api/bootstrap-static/");
            JsonNode root = objectMapper.readTree(bootstrapJson);

            JsonNode playersNode = root.get("elements");

            for (JsonNode playerNode : playersNode) {

                Integer fplId = playerNode.get("id").asInt();
                String firstName = playerNode.get("first_name").asText();
                String lastName = playerNode.get("second_name").asText();
                Integer teamFplId = playerNode.get("team").asInt();
                Integer elementType = playerNode.get("element_type").asInt();
                JsonNode birthNode = playerNode.get("birth_date");


                LocalDate birthDate = null;

                if (birthNode != null && !birthNode.isNull()) {
                    birthDate = LocalDate.parse(birthNode.asText());
                }
                // Trouver le club correspondant
                Club club = clubRepository.findByFplId(teamFplId)
                        .orElseThrow(() -> new RuntimeException("Club not found for FPL ID: " + teamFplId));

                // Vérifier si joueur existe
                Player player = playerRepository.findByIdFpl(fplId)
                        .orElseGet(Player::new);

                player.setIdFpl(fplId);
                player.setFirstName(firstName);
                player.setLastName(lastName);
                player.setPosition(mapPosition(elementType));
                player.setClub(club);

                player.setDateDeNaissance(birthDate);

                Player saved = playerRepository.save(player);

                result.add(new PlayerFplDto(
                        saved.getId(),
                        saved.getIdFpl(),
                        saved.getFirstName(),
                        saved.getLastName(),
                        saved.getPosition(),
                        saved.getClub().getId(),
                        saved.getDateDeNaissance()
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur import joueurs", e);
        }

        return result;
    }

    private String fetchUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private String mapPosition(Integer elementType) {
        return switch (elementType) {
            case 1 -> "GK";
            case 2 -> "DEF";
            case 3 -> "MID";
            case 4 -> "FWD";
            default -> "UNKNOWN";
        };
    }


}
