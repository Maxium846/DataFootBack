package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.MatchEventDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.model.MatchEvent;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.ClubRepository;
import com.dataFoot.ProjetData.repository.MatchEventRepository;
import com.dataFoot.ProjetData.repository.MatchRepository;
import com.dataFoot.ProjetData.repository.PlayersRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class MatchEventService {

    private final MatchEventRepository eventRepo;
    private final MatchRepository matchRepository;
    private final ClubRepository clubRepository;
    private final PlayersRepository playersRepository;
    public MatchEventService(MatchEventRepository eventRepo,
                             MatchRepository matchRepo,
                             PlayersRepository playerRepo, MatchRepository matchRepository, ClubRepository clubRepository, PlayersRepository playersRepository) {
        this.eventRepo = eventRepo;

        this.matchRepository = matchRepository;
        this.clubRepository = clubRepository;
        this.playersRepository = playersRepository;
    }

    // 🔹 Récupérer tous les événements d'un match pour un joueur
    public PlayerStatDto getPlayerStats(int playerId) {

        int goals = eventRepo.countByPlayerIdAndEventTypeIn(
                playerId,
                List.of(EventType.GOAL, EventType.PENALTY_GOAL) // si tu l’as
        );        int yellow = eventRepo.countByPlayerIdAndEventType(playerId, EventType.YELLOW_CARD);
        int red = eventRepo.countByPlayerIdAndEventType(playerId, EventType.RED_CARD);
        int assists = eventRepo.countAssists(
                playerId,
                List.of(EventType.GOAL) // + PENALTY_GOAL si tu le gères
        );        int total = eventRepo.findByPlayerId(playerId).size();



        PlayerStatDto stats = new PlayerStatDto();

        stats.setPlayerId(playerId);
        stats.setGoals(goals);
        stats.setYellowCard(yellow);
        stats.setRedCard(red);
        stats.setTotalEvents(total);
        stats.setAssists(assists);

        return stats;
    }


    public List<MatchEventDto> getEventsByMatchId(Long matchId) {
        List<MatchEvent> events = eventRepo.findByMatchId(matchId);

        return events.stream()
                .map(this::toDto)
                .toList();
    }



    // 🔹 Ajouter un événement
    public MatchEventDto saveEvent(MatchEventDto dto) {
        Match match = matchRepository.findById(dto.getMatchId()).orElseThrow();
        Club club = clubRepository.findById(dto.getClubId()).orElseThrow();
        Player player = playersRepository.findById(dto.getPlayerId()).orElseThrow();


        MatchEvent event = new MatchEvent();
        event.setMatch(match);
        event.setPlayer(player);
        event.setId(dto.getId());
        if( dto.getEventType() != null){
            event.setEventType(EventType.valueOf(dto.getEventType().toUpperCase()));

        }
        event.setClub(club);
        event.setMinute(dto.getMinutes());


        MatchEvent saved = eventRepo.save(event);
        return toDto(saved);
    }


    // 🔹 Mapper entité → DTO
    private MatchEventDto toDto(MatchEvent event) {
        MatchEventDto dto = new MatchEventDto();
        dto.setPlayerId(event.getPlayer().getId());
        dto.setId(event.getId());
        dto.setMatchId(event.getMatch().getId());
        dto.setMinutes(event.getMinute());
        if (event.getEventType() != null) {
            dto.setEventType(event.getEventType().name());
        }        dto.setClubId(event.getClub().getId());
        return dto;
    }
}

