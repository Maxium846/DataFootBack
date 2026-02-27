package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerFplDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.enumeration.Position;
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
}
