package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.MatchLineUp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface MatchLineUpRepository extends JpaRepository<MatchLineUp, Long> {

    List<MatchLineUp> findByMatchId(Long matchId);

    List<MatchLineUp> findByMatchIdAndClubId(Long matchId, Long clubId);
    @Transactional
    @Modifying
    @Query("DELETE FROM MatchLineUp m WHERE m.league.id = :leagueId")
    void deleteByLeagueId(@Param("leagueId")Long leagueId);
}

