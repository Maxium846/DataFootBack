package com.dataFoot.ProjetData.service;


import com.dataFoot.ProjetData.dto.league.LeagueDetailDto;
import com.dataFoot.ProjetData.dto.league.LeagueDto;
import com.dataFoot.ProjetData.mapper.LeagueMapper;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeagueService {

    private final LeagueRepositoryInterface leagueRepositoryInterface;

    public LeagueService(LeagueRepositoryInterface leagueRepositoryInterface) {
        this.leagueRepositoryInterface = leagueRepositoryInterface;
    }


    // Va en base chercher toute les leagues, transforme la en DTO et retourne moi une liste de Dto
    public List <LeagueDto> findAll(){

        return leagueRepositoryInterface.findAll().stream().map(LeagueMapper::toDto).toList();
    }


    public LeagueDto createLeague(LeagueDto leagueDto){
        League league = LeagueMapper.toEntity(leagueDto);
        League saved = leagueRepositoryInterface.save(league);
        return LeagueMapper.toDto(saved);
    }

    public LeagueDetailDto getLeagueDetail (Long id){

        League league = leagueRepositoryInterface.findById(id).orElseThrow(() -> new  RuntimeException(" la lieague n'a pas été trouvé"));

        return LeagueMapper.toDetailDto(league);

    }

    public void removeLeague (Long id){

        if(!leagueRepositoryInterface.existsById(id)){

            throw new RuntimeException("L'id n'existe pas ");
        }
        leagueRepositoryInterface.deleteById(id);
    }
}
