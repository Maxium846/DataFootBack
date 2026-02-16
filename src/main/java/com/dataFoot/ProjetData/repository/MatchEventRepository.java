package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.model.MatchEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEvent,Long> {

    List<MatchEvent> findByMatchId(Long matchId);

    List<MatchEvent> findByPlayerId(Long playerId);

    long countByPlayerIdAndEventType(Long playerId, EventType eventType);



}
