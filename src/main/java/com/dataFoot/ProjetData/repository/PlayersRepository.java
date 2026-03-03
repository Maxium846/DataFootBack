package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlayersRepository extends JpaRepository<Player,Long> {


    List<Player> findByClubId(Long clubId);

    void deleteByClub_League_Id(Long leagueId);
    Optional<Player> findByApiFootballPlayerId(Integer apiFootballPlayerId);


    List<Player> findByApiFootballPlayerIdIn(Collection<Integer> ids);


}
