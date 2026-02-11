package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.MatchEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEvent,Long> {

    List<MatchEvent> findByMatchId(Long matchId);
}
