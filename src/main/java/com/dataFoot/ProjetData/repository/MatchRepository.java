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
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match,Long> {

    List<Match> findByLeagueAndPlayedTrue(League leagueId);
    List<Match> findMatchesByLeagueIdOrderByJourneeAsc(Long leagueId);

    List<Match> findByLeagueId(Long leagueId);

    Optional<Match> findByApiFootballFixtureId(Integer fixtureId);

}

