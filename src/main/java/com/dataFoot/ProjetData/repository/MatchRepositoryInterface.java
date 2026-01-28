package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepositoryInterface extends JpaRepository<Match,Long> {
    List<Match> findByLeagueIdOrderByMatchDateAsc(Long leagueId);

    List<Match> findByLeagueAndPlayedTrue(League leagueId);
    List<Match> findByLeagueIdOrderByJourneeAsc(Long leagueId);
    boolean existsByLeagueId(Long leagueId);
}

