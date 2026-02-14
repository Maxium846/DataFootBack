package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepositoryInterface extends JpaRepository<Club,Long> {


        // Ta méthode existante
        List<Club> findByLeagueId(Long leagueId);

        // Nouvelle méthode pour forcer l'attachement complet des clubs
        @Query("SELECT c FROM Club c JOIN FETCH c.league WHERE c.league.id = :leagueId")
        List<Club> findAllByLeagueIdFetch(@Param("leagueId") Long leagueId);

        Optional<Club> findByFplId(Integer fplId);
    }





