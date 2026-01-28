package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassementRepositoryInterface extends JpaRepository<Classement,Long> {

    List<Classement> findByLeague(League leagueId);
    Classement findByLeagueAndClub(League league, Club club);
    boolean existsByLeagueIdAndClubId(Long leagueId, Long clubId);

}
