package com.datafoot.matchstat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchStatRepository extends JpaRepository<MatchStat, Long> {


    List<MatchStat> findByMatchId(Long matchId);

    Optional<MatchStat> findByMatchIdAndTeamId_Id(Long matchId, Long clubId);

    boolean existsByMatchId(Long matchId);









}


