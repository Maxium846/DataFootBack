package com.dataFoot.matchevent;

import com.dataFoot.match.matchdto.MatchEventDto;
import com.dataFoot.enumeration.EventType;
import com.dataFoot.match.MatchRepository;
import com.dataFoot.matchevent.mapper.MatchEventMapper;
import com.dataFoot.player.PlayersRepository;
import com.dataFoot.team.TeamRepository;
import com.dataFoot.team.Team;
import com.dataFoot.match.Match;
import com.dataFoot.player.Player;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dataFoot.matchevent.mapper.MatchEventMapper.toDto;

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
                .map(MatchEventMapper::toDto)
                .toList();
    }

    public MatchEventDto saveEvent(MatchEventDto dto) {
        Match match = matchRepository.findById(dto.getMatchId()).orElseThrow();
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow();
        Player player = playersRepository.findById(dto.getPlayerId()).orElseThrow();


        MatchEvent event = new MatchEvent();
        event.setMatch(match);
        event.setPlayer(player);
        event.setId(dto.getId());
        if( dto.getEventType() != null){
            event.setEventType(EventType.valueOf(dto.getEventType().toUpperCase()));

        }
        event.setTeam(team);
        event.setMinute(dto.getMinutes());


        MatchEvent saved = eventRepo.save(event);
        return toDto(saved);
    }



}

