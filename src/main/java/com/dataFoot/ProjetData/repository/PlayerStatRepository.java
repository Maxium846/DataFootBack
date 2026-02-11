package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatRepository extends JpaRepository<PlayerStats, Long> {

    PlayerStats findByPlayer_Id(Long joueurId);
}
