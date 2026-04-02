package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlayersRepository extends JpaRepository<Player,Long> {


    List<Player> findByClubId(Long clubId);

    void deleteByClub_League_Id(Long leagueId);
    Optional<Player> findByApiFootballPlayerId(Integer apiFootballPlayerId);


    List<Player> findByApiFootballPlayerIdIn(Collection<Integer> ids);



    @Query("""
       SELECT p
       FROM Player p
       JOIN p.club c
       WHERE c.league.id = :leagueId
       AND c.id = :clubId
       """)
    List<Player> findByLeagueAndClubId(Long leagueId, Long clubId);

    @Query(value = """
    SELECT p.*
    FROM players p
    JOIN clubs c ON p.club_id = c.id
    JOIN (
        SELECT cl.*,\s
               ROW_NUMBER() OVER (PARTITION BY cl.league_id ORDER BY cl.points DESC) as rn
        FROM classement cl
    ) cl ON cl.club_id = c.id
    WHERE cl.rn <= :limit
""", nativeQuery = true)
    List<Player> findTopPlayersPerLeague(@Param("limit") int limit);

}
