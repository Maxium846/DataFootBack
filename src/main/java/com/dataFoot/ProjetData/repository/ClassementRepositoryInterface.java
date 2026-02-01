package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassementRepositoryInterface extends JpaRepository<Classement,Long> {

    List<Classement> findByLeague(League leagueId);
    Optional<Classement> findByLeagueAndClub(League league, Club club);
    boolean existsByLeagueIdAndClubId(Long leagueId, Long clubId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Classement c WHERE c.league.id = :leagueId")
    void deleteByLeagueId(@Param("leagueId") Long leagueId);


}
