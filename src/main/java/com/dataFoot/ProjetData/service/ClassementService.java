package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ClassementService {

 private final LeagueRepositoryInterface leagueRepositoryInterface;

    public ClassementService(LeagueRepositoryInterface leagueRepositoryInterface) {
        this.leagueRepositoryInterface = leagueRepositoryInterface;
    }


    public List<ClassementDto> getClassementByLeagues (Long leagueId){


        League league = leagueRepositoryInterface.findById(leagueId).orElseThrow(()-> new RuntimeException("L'id n'existe pas "));

        return league.getClubs().stream().map(club -> {

            ClassementDto classementDto = new ClassementDto();
            classementDto.setClubId(club.getId());
            classementDto.setClubName(club.getName());
            classementDto.setLosses(0);
            classementDto.setDraws(0);
            classementDto.setWins(0);
            classementDto.setGoalsAgainst(0);
            classementDto.setGoalsFor(0);
            classementDto.setPoints(0);
            classementDto.setGoalDifference(0);


            return  classementDto;
        }).sorted(Comparator.comparingInt(ClassementDto::getPoints).reversed()).toList();


    }
}
