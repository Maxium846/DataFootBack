package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.mapper.ClubMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.repository.ClubRepositoryInterface;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {

    private final ClubRepositoryInterface clubRepositoryInterface;
    private final LeagueRepositoryInterface leagueRepositoryInterface;


    public ClubService(ClubRepositoryInterface clubRepositoryInterface, LeagueRepositoryInterface leagueRepositoryInterface) {
        this.clubRepositoryInterface = clubRepositoryInterface;
        this.leagueRepositoryInterface = leagueRepositoryInterface;
    }

    public ClubDto createClub(ClubDto dto) {

        League league = leagueRepositoryInterface.findById(dto.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found"));


        //« À partir des données du DTO et de la ligue trouvée en base,
        //crée-moi un objet Club prêt à être sauvegardé.
        Club club = ClubMapper.toEntity(dto, league);

        Club saved = clubRepositoryInterface.save(club);

        leagueRepositoryInterface.findById(dto.getLeagueId()).orElseThrow(() -> new RuntimeException("League not found"));


        return ClubMapper.toDto(saved);
    }


    public List<ClubDto> findAll() {
        return clubRepositoryInterface.findAll()
                .stream()
                .map(ClubMapper::toDto)
                .toList();
    }

    public ClubDto findById(Long id) {
        Club club = clubRepositoryInterface.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        return ClubMapper.toDto(club);
    }

    public ClubDto update(Long id, ClubDto dto) {
        Club club = clubRepositoryInterface.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        ClubMapper.updateEntity(club, dto);
        Club updated = clubRepositoryInterface.save(club);

        return ClubMapper.toDto(updated);
    }
    public void delete(Long id) {
        if (!clubRepositoryInterface.existsById(id)) {
            throw new RuntimeException("Club not found");
        }
        clubRepositoryInterface.deleteById(id);
    }

}
