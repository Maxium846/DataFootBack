package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.club.ClubDetailDto;
import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.mapper.ClubMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.repository.ClubRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {

    private final ClubRepositoryInterface clubRepositoryInterface;


    public ClubService(ClubRepositoryInterface clubRepositoryInterface) {
        this.clubRepositoryInterface = clubRepositoryInterface;
    }

    public ClubDto create(ClubDto dto){

        Club club = ClubMapper.toEntity(dto);
        Club saved = clubRepositoryInterface.save(club);
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

    public ClubDetailDto getClubDetail(Long id) {
        Club club = clubRepositoryInterface.findByIdWithPlayers(id)
                .orElseThrow(() -> new RuntimeException("Club non trouvé"));

        // On mappe l'entité vers le DTO
        return ClubMapper.toDetailDto(club);
    }


}
