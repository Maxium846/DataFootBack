package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.player.PlayerStatDto;
import com.dataFoot.ProjetData.mapper.PlayerStatMapper;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.model.PlayerStats;
import com.dataFoot.ProjetData.repository.PlayerStatRepository;
import com.dataFoot.ProjetData.repository.PlayersRepositoryInterface;
import org.springframework.stereotype.Service;

@Service
public class PlayerStatService {


    private final PlayerStatRepository playerStatRepository;
    private final PlayersRepositoryInterface playersRepositoryInterface;

    public PlayerStatService(PlayerStatRepository playerStatRepository, PlayersRepositoryInterface playersRepositoryInterface, PlayersRepositoryInterface playersRepositoryInterface1) {
        this.playerStatRepository = playerStatRepository;
        this.playersRepositoryInterface = playersRepositoryInterface1;
    }


    public PlayerStatDto getStatByJoueurId(Long id){

        Player player = playersRepositoryInterface.findById(id).orElseThrow(()-> new RuntimeException("l'id du jouuer n'existe pas"));

        PlayerStats playerStats = playerStatRepository.findByPlayer_Id(player.getId());

        return PlayerStatMapper.toDto(playerStats);






    }
}
