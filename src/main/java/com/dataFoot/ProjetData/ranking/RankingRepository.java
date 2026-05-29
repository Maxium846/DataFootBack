package com.dataFoot.ProjetData.ranking;

import com.dataFoot.ProjetData.league.League;
import com.dataFoot.ProjetData.team.Teams;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking,Long> {

    @Query("""
    SELECT c
    FROM Ranking c
    JOIN FETCH c.teams
    WHERE c.league.id = :leagueId
    ORDER BY c.points DESC , c.goalDifference DESC
""")
    List<Ranking> findByLeagueIdWithClub(@Param("leagueId") Long leagueId);

    //Optional<Ranking> findByLeagueAndClub(League league, Teams teams);

    //List<Teams> findByClub(Teams teams);
    //boolean existsByLeagueIdAndClubId(Long leagueId, Long clubId);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM Classement c WHERE c.league.id = :leagueId")
//    void deleteByLeagueId(@Param("leagueId") Long leagueId);

    }



