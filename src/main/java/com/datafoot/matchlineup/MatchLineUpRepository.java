package com.datafoot.matchlineup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface MatchLineUpRepository extends JpaRepository<MatchLineUp, Long> {

    List<MatchLineUp> findByMatchId(Long matchId);


    Optional<MatchLineUp> findByMatchIdAndPlayersId(Long matchId, long playerId);


    boolean existsByMatchId(long matchId);

}

