package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayersRepositoryInterface extends JpaRepository<Player,Long> {
}
