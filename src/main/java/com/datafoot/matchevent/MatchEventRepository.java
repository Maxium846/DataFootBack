package com.datafoot.matchevent;

import com.datafoot.enumeration.EventType;
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


    int countByPlayerIdAndEventTypeIn(int playerId, Collection<EventType> types);

    boolean existsByMatchId(Long matchId);

}
