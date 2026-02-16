package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.MatchEventDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.model.MatchEvent;
import com.dataFoot.ProjetData.repository.MatchEventRepository;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import com.dataFoot.ProjetData.repository.PlayersRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchEventService {

    private final MatchEventRepository eventRepo;
    public MatchEventService(MatchEventRepository eventRepo,
                             MatchRepositoryInterface matchRepo,
                             PlayersRepositoryInterface playerRepo) {
        this.eventRepo = eventRepo;

    }

    // 🔹 Récupérer tous les événements d'un match pour un joueur
    public PlayerStatDto getPlayerStats(Long playerId) {

        long goals = eventRepo.countByPlayerIdAndEventType(playerId, EventType.GOAL);
        long assists = eventRepo.countByPlayerIdAndEventType(playerId, EventType.ASSIST);
        long yellow = eventRepo.countByPlayerIdAndEventType(playerId, EventType.YELLOW_CARD);
        long red = eventRepo.countByPlayerIdAndEventType(playerId, EventType.RED_CARD);
        long total = eventRepo.findByPlayerId(playerId).size();

        PlayerStatDto stats = new PlayerStatDto();

        stats.setPlayerId(playerId);
        stats.setGoals(goals);
        stats.setAssists(assists);
        stats.setYellowCard(yellow);
        stats.setRedCard(red);
        stats.setTotalEvents(total);

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
        MatchEvent event = new MatchEvent();
        event.setMatchId(dto.getMatchId());
        event.setPlayerId(dto.getPlayerId());
        event.setId(dto.getId());
        if( dto.getEventType() != null){
            event.setEventType(EventType.valueOf(dto.getEventType().toUpperCase()));

        }
        event.setClubId(dto.getClubId());
        event.setMinute(dto.getMinutes());
        event.setClubId(dto.getClubId());


        MatchEvent saved = eventRepo.save(event);
        return toDto(saved);
    }

    // 🔹 Mapper entité → DTO
    private MatchEventDto toDto(MatchEvent event) {
        MatchEventDto dto = new MatchEventDto();
        dto.setPlayerId(event.getPlayerId());
        dto.setId(event.getId());
        dto.setMatchId(event.getMatchId());
        dto.setMinutes(event.getMinute());
        if (event.getEventType() != null) {
            dto.setEventType(event.getEventType().name());
        }        dto.setClubId(event.getClubId());
        return dto;
    }
}

