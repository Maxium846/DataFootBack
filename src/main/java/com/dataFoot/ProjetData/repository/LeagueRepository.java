package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.League;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface LeagueRepository extends JpaRepository<League,Long> {

}
