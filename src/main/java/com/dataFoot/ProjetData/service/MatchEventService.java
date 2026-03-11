package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.MatchEventDto;
import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.model.MatchEvent;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchEventService {

    private final MatchEventRepository eventRepo;
    private final MatchRepository matchRepository;
    private final ClubRepository clubRepository;
    private final PlayersRepository playersRepository;

    private final PlayerStatRepository playerStatRepository;
    public MatchEventService(MatchEventRepository eventRepo,
                             MatchRepository matchRepo,
                             PlayersRepository playerRepo, MatchRepository matchRepository, ClubRepository clubRepository, PlayersRepository playersRepository, PlayerStatRepository playerStatRepository) {
        this.eventRepo = eventRepo;

        this.matchRepository = matchRepository;
        this.clubRepository = clubRepository;
        this.playersRepository = playersRepository;
        this.playerStatRepository = playerStatRepository;
    }

    // 🔹 Récupérer tous les événements d'un match pour un joueur



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

        dto.setId(event.getId());
        dto.setMatchId(event.getMatch().getId());
        dto.setMinutes(event.getMinute());
        dto.setClubId(event.getClub().getId());

        if (event.getEventType() != null) {
            dto.setEventType(event.getEventType().name());
        }

        if (event.getPlayer() != null) {
            dto.setPlayerId(event.getPlayer().getId());
        }

        if (event.getAssistPlayer() != null) {
            dto.setAssistPlayerId(event.getAssistPlayer().getId());
        }
        if (event.getAssistPlayer() != null) {
            dto.setAssistName(event.getAssistName());
        }

        if (event.getPlayerOut() != null) {
            dto.setPlayerOutId(event.getPlayerOut().getId());
            dto.setNamePlayerOut(event.getPlayerOutName());
        }

        if (event.getPlayerIn() != null) {
            dto.setPlayerInId(event.getPlayerIn().getId());
            dto.setNamePlayerin(event.getPlayerInName());
        }


        return dto;
    }
}

