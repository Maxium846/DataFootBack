package com.dataFoot.ProjetData.service;


import com.dataFoot.ProjetData.dto.league.LeagueAffichageDto;
import com.dataFoot.ProjetData.dto.league.LeagueByIdDto;
import com.dataFoot.ProjetData.dto.league.LeagueDto;
import com.dataFoot.ProjetData.mapper.LeagueMapper;
import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.ClassementRepositoryInterface;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeagueService {

    private final LeagueRepositoryInterface leagueRepositoryInterface;

    public LeagueService(LeagueRepositoryInterface leagueRepositoryInterface, MatchRepositoryInterface matchRepositoryInterface, ClassementRepositoryInterface classementRepositoryInterface) {
        this.leagueRepositoryInterface = leagueRepositoryInterface;

    }


    // Va en base chercher toute les leagues, transforme la en DTO et retourne moi une liste de Dto
    public List <LeagueAffichageDto> findAll(){

        return leagueRepositoryInterface.findAll().stream().map(LeagueMapper::toDtoAfichage).toList();
    }


    public LeagueDto createLeague(LeagueDto leagueDto){
        League league = LeagueMapper.toEntity(leagueDto);
        League saved = leagueRepositoryInterface.save(league);
        return LeagueMapper.toDto(saved);
    }

    public LeagueByIdDto getLeagueById (Long id){

        League league = leagueRepositoryInterface.findById(id).orElseThrow(() -> new  RuntimeException(" la ligue n'a pas été trouvé"));

        return LeagueMapper.toDetailDto(league);

    }

    public void removeLeague (Long id){

        if(!leagueRepositoryInterface.existsById(id)){

            throw new RuntimeException("L'id n'existe pas ");
        }
        leagueRepositoryInterface.deleteById(id);
    }



    }

