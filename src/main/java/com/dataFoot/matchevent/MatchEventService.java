package com.dataFoot.matchevent;

import com.dataFoot.match.matchdto.MatchEventDto;
import com.dataFoot.enumeration.EventType;
import com.dataFoot.match.MatchRepository;
import com.dataFoot.player.PlayersRepository;
import com.dataFoot.team.TeamRepository;
import com.dataFoot.team.Teams;
import com.dataFoot.match.Match;
import com.dataFoot.player.Player;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchEventService {

    private final MatchEventRepository eventRepo;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayersRepository playersRepository;

    public MatchEventService(MatchEventRepository eventRepo

            , MatchRepository matchRepository, TeamRepository teamRepository, PlayersRepository playersRepository) {
        this.eventRepo = eventRepo;

        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.playersRepository = playersRepository;
    }




    public List<MatchEventDto> getEventsByMatchId(Long matchId) {
        List<MatchEvent> events = eventRepo.findByMatchId(matchId);

        return events.stream()
                .map(this::toDto)
                .toList();
    }

    public MatchEventDto saveEvent(MatchEventDto dto) {
        Match match = matchRepository.findById(dto.getMatchId()).orElseThrow();
        Teams teams = teamRepository.findById(dto.getTeamId()).orElseThrow();
        Player player = playersRepository.findById(dto.getPlayerId()).orElseThrow();


        MatchEvent event = new MatchEvent();
        event.setMatch(match);
        event.setPlayer(player);
        event.setId(dto.getId());
        if( dto.getEventType() != null){
            event.setEventType(EventType.valueOf(dto.getEventType().toUpperCase()));

        }
        event.setTeams(teams);
        event.setMinute(dto.getMinutes());


        MatchEvent saved = eventRepo.save(event);
        return toDto(saved);
    }


    private MatchEventDto toDto(MatchEvent event) {

        MatchEventDto dto = new MatchEventDto();

        dto.setId(event.getId());
        dto.setMatchId(event.getMatch().getId());
        dto.setMinutes(event.getMinute());
        dto.setTeamId(event.getTeams().getId());

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

