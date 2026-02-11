package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.MatchLineUpDto;
import com.dataFoot.ProjetData.model.MatchLineUp;
import com.dataFoot.ProjetData.repository.ClubRepositoryInterface;
import com.dataFoot.ProjetData.repository.MatchLineUpRepository;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import com.dataFoot.ProjetData.repository.PlayersRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchLineUpService {

    private final MatchLineUpRepository lineupRepo;
    private final MatchRepositoryInterface matchRepo;
    private final PlayersRepositoryInterface playerRepo;
    private final ClubRepositoryInterface clubRepo;

    public MatchLineUpService(MatchLineUpRepository lineupRepo,
                              MatchRepositoryInterface matchRepo,
                              PlayersRepositoryInterface playerRepo,
                              ClubRepositoryInterface clubRepo) {
        this.lineupRepo = lineupRepo;
        this.matchRepo = matchRepo;
        this.playerRepo = playerRepo;
        this.clubRepo = clubRepo;
    }

    // 🔹 Récupérer la composition d'un match
    public List<MatchLineUpDto> getLineUpByMatch(Long matchId) {
        return lineupRepo.findByMatchId(matchId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 🔹 Ajouter une ligne dans la composition
    public List<MatchLineUpDto> saveLineups(Long matchId, List<MatchLineUpDto> dtos) {
        List<MatchLineUpDto> savedDtos = new ArrayList<>();

        for (MatchLineUpDto dto : dtos) {
            MatchLineUp lineup = new MatchLineUp();
            lineup.setMatch(matchRepo.getReferenceById(matchId));
            lineup.setPlayer(playerRepo.getReferenceById(dto.getPlayerId()));
            lineup.setClub(clubRepo.getReferenceById(dto.getClubId()));
            lineup.setPosition(dto.getPosition());
            lineup.setStarter(dto.getStarter() != null ? dto.getStarter() : true);

            MatchLineUp saved = lineupRepo.save(lineup);
            savedDtos.add(toDto(saved));
        }

        return savedDtos;
    }


    // 🔹 Mapper entité → DTO
    private MatchLineUpDto toDto(MatchLineUp lineup) {
        MatchLineUpDto dto = new MatchLineUpDto();
        dto.setId(lineup.getId());
        dto.setPlayerId(lineup.getPlayer().getId());
        dto.setPlayerName(lineup.getPlayer().getFirstName() + " " + lineup.getPlayer().getLastName());
        dto.setClubId(lineup.getClub().getId());
        dto.setPosition(lineup.getPosition());
        dto.setStarter(lineup.getStarter());
        dto.setMatchId(lineup.getMatch().getId());
        return dto;
    }
}
