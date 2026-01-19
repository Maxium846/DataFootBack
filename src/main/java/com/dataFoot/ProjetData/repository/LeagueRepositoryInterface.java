package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.League;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepositoryInterface extends JpaRepository<League,Long> {
}
