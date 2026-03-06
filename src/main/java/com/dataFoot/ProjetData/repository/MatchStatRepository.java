package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.MatchStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchStatRepository extends JpaRepository<MatchStat, Long> {


    List< MatchStat> findByMatchId(Long matchId);
}
