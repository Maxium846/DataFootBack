package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepositoryInterface extends JpaRepository<Club,Long> {


}
