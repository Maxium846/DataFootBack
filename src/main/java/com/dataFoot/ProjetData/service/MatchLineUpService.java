package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.MatchLineUpDto;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.MatchLineUp;
import com.dataFoot.ProjetData.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchLineUpService {

    private final MatchLineUpRepository lineupRepo;
    private final MatchRepository matchRepo;
    private final PlayersRepository playerRepo;
    private final ClubRepository clubRepo;

    private final LeagueRepository leagueRepository;

    public MatchLineUpService(MatchLineUpRepository lineupRepo,
                              MatchRepository matchRepo,
                              PlayersRepository playerRepo,
                              ClubRepository clubRepo, LeagueRepository leagueRepository) {
        this.lineupRepo = lineupRepo;
        this.matchRepo = matchRepo;
        this.playerRepo = playerRepo;
        this.clubRepo = clubRepo;
        this.leagueRepository = leagueRepository;
    }

    // 🔹 Récupérer la composition d'un match
    public List<MatchLineUpDto> getLineUpByMatch(Long matchId) {
        return lineupRepo.findByMatchId(matchId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 🔹 Ajouter une ligne dans la composition
    public List<MatchLineUpDto> saveLineups(Long matchId, List<MatchLineUpDto> dtos,Long leagueId) {
        List<MatchLineUpDto> savedDtos = new ArrayList<>();
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));
        for (MatchLineUpDto dto : dtos) {
            MatchLineUp lineup = new MatchLineUp();
            lineup.setMatch(matchRepo.getReferenceById(matchId));
            lineup.setPlayer(playerRepo.getReferenceById(dto.getPlayerId()));
            lineup.setClub(clubRepo.getReferenceById(dto.getClubId()));
            lineup.setPosition(dto.getPosition());
            lineup.setStarter(dto.getStarter() != null ? dto.getStarter() : true);
            lineup.setLeague(league);
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
        dto.setLeagueId(lineup.getLeague().getId());
        dto.setPlayerName(lineup.getPlayer().getFirstName() + " " + lineup.getPlayer().getLastName());
        dto.setClubId(lineup.getClub().getId());
        dto.setPosition(lineup.getPosition());
        dto.setStarter(lineup.getStarter());
        dto.setMatchId(lineup.getMatch().getId());
        return dto;
    }
}
