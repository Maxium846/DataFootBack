package com.dataFoot.match;

import com.dataFoot.league.League;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match,Long> {

    List<Match> findByLeagueAndPlayedTrue(League leagueId);
    List<Match> findMatchesByLeagueIdOrderByJourneeAsc(Long leagueId);

    List<Match> findByLeagueId(Long leagueId);

    Optional<Match> findByApiFootballFixtureId(Integer fixtureId);

}

