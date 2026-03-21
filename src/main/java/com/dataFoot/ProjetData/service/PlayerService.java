package com.dataFoot.ProjetData.service;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatClassementDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.mapper.PlayerMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.model.PlayerStats;
import com.dataFoot.ProjetData.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {

    private final PlayersRepository playerRepository;
    private final ClubRepository clubRepository;

    private final MatchEventRepository eventRepo;

    private final MatchStatRepository matchStatRepository;
    private final PlayerStatRepository playerStatRepository;

    public PlayerService(PlayersRepository playerRepository, ClubRepository clubRepository, MatchEventRepository eventRepo, MatchStatRepository matchStatRepository, PlayerStatRepository playerStatRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;

        this.eventRepo = eventRepo;
        this.matchStatRepository = matchStatRepository;
        this.playerStatRepository = playerStatRepository;
    }


    public List<PlayerInClubDto> allPlayer(Long clubId) {

        Optional<Club> club = clubRepository.findById(clubId);

        return playerRepository.findByClubId(club.orElseThrow().getId())
                .stream()
                .map(PlayerMapper::toInClubDto)
                .toList();
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


    public List<PlayerStatClassementDto> getStat(Long leagueId) {

        List<Club> clubs = clubRepository.findByLeagueId(leagueId);
        List<PlayerStatClassementDto> joueur = new ArrayList<>();

        for (Club c : clubs) {

            List<Player> players = playerRepository.findByClubId(c.getId());
            for (Player p : players) {
                int countBut = matchStatRepository.sumTotalGoalByPlayerId(p.getId());
                PlayerStatClassementDto playerStatClassementDto = new PlayerStatClassementDto();
                playerStatClassementDto.setTotalBut(countBut);
                playerStatClassementDto.setPlayerId(p.getId());
                playerStatClassementDto.setName(p.getFirstName());
                if (p.getClub() != null) {
                    playerStatClassementDto.setClubName(p.getClub().getName());
                }
                joueur.add(playerStatClassementDto);


            }

        }
        return joueur.stream().sorted(Comparator.comparing(PlayerStatClassementDto::getTotalBut).reversed()).filter(j -> j.getTotalBut()>7).toList();
    }

}