package com.dataFoot.ranking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingRepository extends JpaRepository<Ranking,Long> {

    @Query("""
    SELECT c
    FROM Ranking c
    JOIN FETCH c.team
    WHERE c.league.id = :leagueId
    ORDER BY c.points DESC , c.goalDifference DESC
""")
    List<Ranking> findByLeagueIdWithClub(@Param("leagueId") Long leagueId);

    boolean existsByLeagueIdAndTeamId(Long leagueId, Long teamId);

    //Optional<Ranking> findByLeagueAndClub(League league, Teams team);

    //List<Teams> findByClub(Teams team);
    //boolean existsByLeagueIdAndClubId(Long leagueId, Long clubId);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM Classement c WHERE c.league.id = :leagueId")
//    void deleteByLeagueId(@Param("leagueId") Long leagueId);

    }



