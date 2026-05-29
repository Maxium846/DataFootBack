package com.dataFoot.ProjetData.player;

import com.dataFoot.ProjetData.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlayersRepository extends JpaRepository<Player,Long> {


    List<Player> findByTeamsId(Long clubId);

    Optional<Player> findByApiFootballPlayerId(long apiFootballPlayerId);


    List<Player> findByApiFootballPlayerIdIn(Collection<Integer> ids);



    @Query("""
       SELECT p
       FROM Player p
       JOIN p.teams c
       WHERE c.league.id = :leagueId
       AND c.id = :clubId
       """)
    List<Player> findByLeagueAndClubId(Long leagueId, Long clubId);

    @Query(value = """
    SELECT p.*
    FROM player p
    JOIN teams t ON p.team_id = t.id
    JOIN (
        SELECT r.*,\s
               ROW_NUMBER() OVER (PARTITION BY r.league_id ORDER BY r.points DESC) as rn
        FROM ranking r
    ) r ON r.team_id = t.id
    WHERE r.rn <= :limit
""", nativeQuery = true)
    List<Player> findTopPlayersPerLeague(@Param("limit") int limit);

}
