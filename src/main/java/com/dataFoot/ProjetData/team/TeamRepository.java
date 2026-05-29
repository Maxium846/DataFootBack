package com.dataFoot.ProjetData.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Teams,Long> {


        List<Teams> findByLeagueId(Long leagueId);

        // Nouvelle méthode pour forcer l'attachement complet des clubs
       // @Query("SELECT c FROM Club c JOIN FETCH c.league WHERE c.league.id = :leagueId")
        //List<Teams> findAllByLeagueIdFetch(@Param("leagueId") Long leagueId);

        Optional<Teams> findByApiFootballTeamId(Long apiFootballTeamId);

        Optional<Teams> findByLeagueIdAndApiFootballTeamId(Long leagueId, Long apiFootballTeamId);



}





