package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.dto.player.PlayerStatClassementDto;
import com.dataFoot.ProjetData.model.MatchStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface MatchStatRepository extends JpaRepository<MatchStat, Long> {


    List<MatchStat> findByMatchId(Long matchId);

    Optional<MatchStat> findByMatchIdAndClubId_Id(Long matchId, Long clubId);



    @Query("""
SELECT SUM(ps.totalGoal)
FROM PlayerStats ps
WHERE ps.player.id = :playerId
""")
    int countByPlayerIdTotalGoal(@Param("playerId") Long playerId);


    @Query("""
       select coalesce(sum(ps.totalGoal), 0)
       from PlayerStats ps
       where ps.player.id = :playerId
       """)
    int sumTotalGoalByPlayerId(@Param("playerId") Long playerId);

}


