package com.dataFoot.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {


        List<Team> findByLeagueId(Long leagueId);

        // Nouvelle méthode pour forcer l'attachement complet des clubs
       // @Query("SELECT c FROM Club c JOIN FETCH c.league WHERE c.league.id = :leagueId")
        //List<Teams> findAllByLeagueIdFetch(@Param("leagueId") Long leagueId);

        Optional<Team> findByApiFootballTeamId(Long apiFootballTeamId);

        Optional<Team> findByLeagueIdAndApiFootballTeamId(Long leagueId, Long apiFootballTeamId);



}





