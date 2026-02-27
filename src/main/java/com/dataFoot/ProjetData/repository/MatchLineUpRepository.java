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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from MatchLineUp ml where ml.match.id = :matchId")
    void deleteAllByMatchId(@Param("matchId") Long matchId);
}

