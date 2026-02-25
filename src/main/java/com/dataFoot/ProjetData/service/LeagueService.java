package com.dataFoot.ProjetData.service;


import com.dataFoot.ProjetData.dto.league.LeagueAffichageDto;
import com.dataFoot.ProjetData.dto.league.LeagueByIdDto;
import com.dataFoot.ProjetData.dto.league.LeagueDto;
import com.dataFoot.ProjetData.mapper.LeagueMapper;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.repository.ClassementRepository;
import com.dataFoot.ProjetData.repository.LeagueRepository;
import com.dataFoot.ProjetData.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;

    public LeagueService(LeagueRepository leagueRepository, MatchRepository matchRepository, ClassementRepository classementRepository) {
        this.leagueRepository = leagueRepository;

    }


    // Va en base chercher toute les leagues, transforme la en DTO et retourne moi une liste de Dto
    public List <LeagueAffichageDto> findAll(){

        return leagueRepository.findAll().stream().map(LeagueMapper::toDtoAfichage).toList();
    }


    public LeagueDto createLeague(LeagueDto leagueDto){
        League league = LeagueMapper.toEntity(leagueDto);
        League saved = leagueRepository.save(league);
        return LeagueMapper.toDto(saved);
    }

    public LeagueByIdDto getLeagueById (Long id){

        League league = leagueRepository.findById(id).orElseThrow(() -> new  RuntimeException(" la ligue n'a pas été trouvé"));

        return LeagueMapper.toDetailDto(league);

    }

    public void removeLeague (Long id){

        if(!leagueRepository.existsById(id)){

            throw new RuntimeException("L'id n'existe pas ");
        }
        leagueRepository.deleteById(id);
    }



    }

