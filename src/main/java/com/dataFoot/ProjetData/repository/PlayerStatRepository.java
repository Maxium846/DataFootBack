package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.MatchEvent;
import com.dataFoot.ProjetData.model.MatchLineUp;
import com.dataFoot.ProjetData.model.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlayerStatRepository extends JpaRepository<PlayerStats, Long> {


    PlayerStats findByPlayer_Id(Long joueurId);
}
