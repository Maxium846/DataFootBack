package com.dataFoot.matchstat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchStatRepository extends JpaRepository<MatchStat, Long> {


    List<MatchStat> findByMatchId(Long matchId);

    Optional<MatchStat> findByMatchIdAndTeamsId_Id(Long matchId, Long clubId);








}


