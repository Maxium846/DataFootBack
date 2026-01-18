package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClubRepositoryInterface extends JpaRepository<Club,Long> {

    @Query("""
    SELECT c FROM Club c
    LEFT JOIN FETCH c.player
    WHERE c.id = :id
""")
    Optional<Club> findByIdWithPlayers(@Param("id") Long id);



}
