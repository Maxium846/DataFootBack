package com.dataFoot.matchlineup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface MatchLineUpRepository extends JpaRepository<MatchLineUp, Long> {

    List<MatchLineUp> findByMatchId(Long matchId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from MatchLineUp ml where ml.match.id = :matchId")
    void deleteAllByMatchId(@Param("matchId") Long matchId);

    Optional<MatchLineUp> findByMatchIdAndPlayersId(Long matchId, long playerId);

    void deleteByMatchIdAndPlayersIdNotIn(Long matchId, java.util.Collection<Long> playerIds);

    boolean existsByMatchId(long matchId);

}

