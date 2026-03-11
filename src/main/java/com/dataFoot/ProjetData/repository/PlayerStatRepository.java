package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.model.MatchEvent;
import com.dataFoot.ProjetData.model.MatchLineUp;
import com.dataFoot.ProjetData.model.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerStatRepository extends JpaRepository<PlayerStats, Long> {


    PlayerStats findByPlayer_Id(Long joueurId);

    @Query("""
SELECT COUNT(ps)
FROM PlayerStats ps
WHERE ps.player.id = :playerId
AND ps.minutePlayed > 0
""")
    int countByPlayerIdMatchPlayed(@Param("playerId") int playerId);

    @Query("""
SELECT SUM(ps.minutePlayed)
FROM PlayerStats ps
WHERE ps.player.id = :playerId
""")
    int countByPlayerIdMinutesPlayed(@Param("playerId") int playerId);


}
