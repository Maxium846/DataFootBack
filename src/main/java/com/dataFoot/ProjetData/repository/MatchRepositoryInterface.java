package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepositoryInterface extends JpaRepository<Match,Long> {
    List<Match> findByLeagueIdOrderByMatchDateAsc(Long leagueId);

    List<Match> findByLeagueAndPlayedTrue(League leagueId);
    List<Match> findMatchesByLeagueIdOrderByJourneeAsc(Long leagueId);
    boolean existsByLeagueId(Long leagueId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Match m WHERE m.league.id = :leagueId")
    void deleteByLeagueId(@Param("leagueId") Long leagueId);
}

