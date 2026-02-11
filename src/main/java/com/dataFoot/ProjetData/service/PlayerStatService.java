package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.mapper.PlayerStatMapper;
import com.dataFoot.ProjetData.model.PlayerStats;
import com.dataFoot.ProjetData.repository.PlayerStatRepository;
import com.dataFoot.ProjetData.repository.PlayersRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class PlayerStatService {


    private final PlayerStatRepository playerStatRepository;

    public PlayerStatService(PlayerStatRepository playerStatRepository, PlayersRepositoryInterface playersRepositoryInterface) {
        this.playerStatRepository = playerStatRepository;
    }


    public PlayerStatDto getStatByJoueurId(Long id){

PlayerStats playerStats = playerStatRepository.findByPlayer_Id(id);
return PlayerStatMapper.toDto(playerStats);


    }
}
