package com.dataFoot.ProjetData.service;
import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.mapper.PlayerMapper;
import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {

    private final PlayersRepository playerRepository;
    private final ClubRepository clubRepository;

    private final MatchEventRepository eventRepo;

    private final PlayerStatRepository playerStatRepository;
   private final  ClassementRepository classementRepository;
   private final LeagueRepository leagueRepository;

    public PlayerService(PlayersRepository playerRepository, ClubRepository clubRepository, MatchEventRepository eventRepo, PlayerStatRepository playerStatRepository, ClassementRepository classementRepository, LeagueRepository leagueRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;

        this.eventRepo = eventRepo;
        this.playerStatRepository = playerStatRepository;
        this.classementRepository = classementRepository;
        this.leagueRepository = leagueRepository;
    }


    public List<PlayerInClubDto> allPlayer(Long clubId) {

        Optional<Club> club = clubRepository.findById(clubId);

        return playerRepository.findByClubId(club.orElseThrow().getId())
                .stream()
                .map(PlayerMapper::toInClubDto)
                .toList();
    }
    public List<PlayerDto> allPlayerInAllLeague(){

        return playerRepository.findAll().stream().map(PlayerMapper::toDto).toList();

    }

    public List<PlayerDto> getPlayerByClubByClassement() {

        List<League> league = leagueRepository.findAll();
        List<Player> p1 = new ArrayList<>();

        for(League l : league){
            List<Classement> classement = classementRepository.findByLeagueIdWithClub(l.getId()).stream().limit(8).toList();

            for (Classement c1 : classement) {
                List<Player> players = playerRepository.findByClubId(c1.getClub().getId());
                p1.addAll(players);
            }
        }


        return p1.stream().map(PlayerMapper::toDto).toList();



    }
    public PlayerInClubDto getPlayerById(long id) {

        Player player = playerRepository.findById(id).orElseThrow(() -> new RuntimeException("l'id du joueur n'esiste pas "));

        return PlayerMapper.toInClubDto(player);

    }

    public PlayerStatDto getPlayerStats(int playerId) {

        int goals = eventRepo.countByPlayerIdAndEventTypeIn(
                playerId,
                List.of(EventType.GOAL, EventType.PENALTY_GOAL) // si tu l’as
        );
        int yellow = eventRepo.countByPlayerIdAndEventType(playerId, EventType.YELLOW_CARD);
        int red = eventRepo.countByPlayerIdAndEventType(playerId, EventType.RED_CARD);
        int assists = eventRepo.countAssists(
                playerId,
                List.of(EventType.GOAL));
        int total = eventRepo.findByPlayerId(playerId).size();

        int matchesPlayed = playerStatRepository.countByPlayerIdMatchPlayed(playerId);
        int minutePlayed = playerStatRepository.countByPlayerIdMinutesPlayed(playerId);


        PlayerStatDto stats = new PlayerStatDto();

        stats.setPlayerId(playerId);
        stats.setGoals(goals);
        stats.setYellowCard(yellow);
        stats.setRedCard(red);
        stats.setTotalEvents(total);
        stats.setAssists(assists);
        stats.setMatchesPlayed(matchesPlayed);
        stats.setMinutesPlayed(minutePlayed);

        return stats;
    }






}