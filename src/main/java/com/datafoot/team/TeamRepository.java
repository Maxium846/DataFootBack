package com.datafoot.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {


        List<Team> findByLeagueId(Long leagueId);

        Optional<Team> findByApiFootballTeamId(Long apiFootballTeamId);

        Optional<Team> findByLeagueIdAndApiFootballTeamId(Long leagueId, Long apiFootballTeamId);



}





