package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.MatchLineUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchLineUpRepository extends JpaRepository<MatchLineUp, Long> {

    List<MatchLineUp> findByMatchId(Long matchId);
    List<MatchLineUp> findByMatchIdAndClubId(Long matchId, Long clubId);
}
