package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayersRepositoryInterface extends JpaRepository<Player,Long> {


    List<Player> findByClubId(Long clubId);
}
