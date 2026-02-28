package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.model.MatchEvent;
import com.dataFoot.ProjetData.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MatchEventRepository extends JpaRepository<MatchEvent,Long> {

    List<MatchEvent> findByMatchId(Long matchId);

    List<MatchEvent> findByPlayerId(int playerId);

    int countByPlayerIdAndEventType(int playerId, EventType eventType);
    @Query("""
       select count(me)
       from MatchEvent me
       where me.assistPlayer.id = :playerId
         and me.eventType in :goalTypes
       """)
    int countAssists(@Param("playerId") int playerId,
                     @Param("goalTypes") List<EventType> goalTypes);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from MatchEvent me where me.match.id = :matchId")
    void deleteAllByMatchId(@Param("matchId") Long matchId);

    int countByPlayerIdAndEventTypeIn(int playerId, Collection<EventType> types);

    Optional<MatchEvent> findByMatchIdAndPlayerId(Long matchId, Long playerId);
    boolean existsByMatchId(Long matchId);

}
