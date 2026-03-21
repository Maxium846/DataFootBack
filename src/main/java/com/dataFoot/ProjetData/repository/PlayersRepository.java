package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}
