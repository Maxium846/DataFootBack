package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.MatchEventDto;
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
    private final MatchRepositoryInterface matchRepo;
    private final PlayersRepositoryInterface playerRepo;

    public MatchEventService(MatchEventRepository eventRepo,
                             MatchRepositoryInterface matchRepo,
                             PlayersRepositoryInterface playerRepo) {
        this.eventRepo = eventRepo;
        this.matchRepo = matchRepo;
        this.playerRepo = playerRepo;
    }

    // 🔹 Récupérer tous les événements d'un match
    public List<MatchEventDto> getEventsByMatch(Long matchId) {
        return eventRepo.findByMatchId(matchId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 🔹 Ajouter un événement
    public MatchEventDto saveEvent(MatchEventDto dto) {
        MatchEvent event = new MatchEvent();
        event.setMatch(matchRepo.getReferenceById(dto.getMatchId()));
        event.setPlayer(playerRepo.getReferenceById(dto.getPlayerId()));

        if (dto.getAssistPlayerId() != null) {
            event.setAssistPlayer(playerRepo.getReferenceById(dto.getAssistPlayerId()));
        }

        event.setType(dto.getType());
        event.setMinute(dto.getMinute());

        MatchEvent saved = eventRepo.save(event);
        return toDto(saved);
    }

    // 🔹 Mapper entité → DTO
    private MatchEventDto toDto(MatchEvent event) {
        MatchEventDto dto = new MatchEventDto();
        dto.setId(event.getId());
        dto.setPlayerId(event.getPlayer().getId());
        dto.setPlayerName(event.getPlayer().getFirstName() + " " + event.getPlayer().getLastName());
        if (event.getAssistPlayer() != null) {
            dto.setAssistPlayerId(event.getAssistPlayer().getId());
            dto.setAssistPlayerName(event.getAssistPlayer().getFirstName() + " " + event.getAssistPlayer().getLastName());
        }
        dto.setType(event.getType());
        dto.setMinute(event.getMinute());
        dto.setMatchId(event.getMatch().getId());
        return dto;
    }
}

