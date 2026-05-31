package com.datafoot.player;
import com.datafoot.exception.entitexception.PlayerNotFoundException;
import com.datafoot.exception.entitexception.TeamNotFoundException;
import com.datafoot.matchevent.MatchEventRepository;
import com.datafoot.player.dto.PlayerDto;
import com.datafoot.player.dto.PlayerInClubDto;
import com.datafoot.player.mapper.PlayerMapper;
import com.datafoot.playerstat.playerstatdto.PlayerStatDto;
import com.datafoot.enumeration.EventType;
import com.datafoot.league.League;
import com.datafoot.league.LeagueRepository;
import com.datafoot.playerstat.PlayerStatRepository;
import com.datafoot.ranking.Ranking;
import com.datafoot.ranking.RankingRepository;
import com.datafoot.team.TeamRepository;
import com.datafoot.team.Team;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {

    private final PlayersRepository playerRepository;
    private final TeamRepository teamRepository;

    private final MatchEventRepository eventRepo;

    private final PlayerStatRepository playerStatRepository;
    private final RankingRepository rankingRepository;
    private final LeagueRepository leagueRepository;

    public PlayerService(PlayersRepository playerRepository, TeamRepository teamRepository, MatchEventRepository eventRepo, PlayerStatRepository playerStatRepository, RankingRepository rankingRepository, LeagueRepository leagueRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;

        this.eventRepo = eventRepo;
        this.playerStatRepository = playerStatRepository;
        this.rankingRepository = rankingRepository;
        this.leagueRepository = leagueRepository;
    }


    public List<PlayerInClubDto> allPlayer(Long clubId) {

        Team team = teamRepository.findById(clubId).orElseThrow(()->new TeamNotFoundException("L'équipe n'existe pas en base"));

        return playerRepository.findByTeamId(team.getId())
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
            List<Ranking> ranking = rankingRepository.findByLeagueIdWithClub(l.getId()).stream().limit(8).toList();

            for (Ranking c1 : ranking) {
                List<Player> players = playerRepository.findByTeamId(c1.getTeam().getId());
                p1.addAll(players);
            }
        }


        return p1.stream().map(PlayerMapper::toDto).toList();



    }

    public List<PlayerDto> getPlayerByDifficultyOpti (String difficulte) {
        int limit = switch (difficulte){

            case "Facile" -> 5;
            case "Intermediaire" -> 10 ;
            case "Difficile" -> 20 ;
            default -> 0;
        };

        List<Player> players = playerRepository.findTopPlayersPerLeague(limit);

        return players.stream().map(PlayerMapper::toDto).toList();

    }

        public PlayerInClubDto getPlayerById(long id) {

        Player player = playerRepository.findById(id).orElseThrow(() -> new PlayerNotFoundException("l'id du joueur n'éxiste pas "));

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

        int matchesPlayed = playerStatRepository.countByPlayersIdMatchPlayed(playerId);
        int minutePlayed = playerStatRepository.countByPlayersIdMinutesPlayed(playerId);


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