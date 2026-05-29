package com.dataFoot.ProjetData.league;


import com.dataFoot.ProjetData.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.ProjetData.league.dto.LeagueAffichageDto;
import com.dataFoot.ProjetData.league.dto.LeagueByIdDto;
import com.dataFoot.ProjetData.league.dtoapi.ResponseDto;
import com.dataFoot.ProjetData.league.dtoapi.ResponseItemsDto;
import com.dataFoot.ProjetData.league.mapper.LeagueMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;
    private final RestClient apiSportClient;

    public LeagueService(LeagueRepository leagueRepository, RestClient apiSportClient) {
        this.leagueRepository = leagueRepository;

        this.apiSportClient = apiSportClient;
    }


    public List<LeagueAffichageDto> findAll() {

        return leagueRepository.findAll().stream().map(LeagueMapper::toDtoAfichage).toList();
    }


    public LeagueByIdDto getLeagueById(Long id) {

        League league = leagueRepository.findById(id).orElseThrow(() -> new LeagueNotFoundException("Cette ligue n'existe pas"));

        return LeagueMapper.toDetailDto(league);

    }

    @Transactional
    public LeagueAffichageDto importLeagueByApiFootball(int leagueApiId) {
        String path = "/leagues?id=" + leagueApiId;
        ResponseDto response = callApi(path);

        if (response == null || response.getResponse() == null || response.getResponse().isEmpty()) {


            throw new LeagueNotFoundException("League" + leagueApiId + "introuvable dans api football");
        }

        ResponseItemsDto responseItemsDto = response.getResponse().get(0);

        League league = leagueRepository.findByApiFootballLeague(leagueApiId).orElseGet(League::new);
        league.setApiFootballLeague(responseItemsDto.getLeague().getId());
        league.setName(responseItemsDto.getLeague().getName());
        league.setCountry(responseItemsDto.getCountry().getName());
        league.setLogo(responseItemsDto.getLeague().getLogo());
        league.setFlag(responseItemsDto.getCountry().getFlag());
        leagueRepository.save(league);


        return LeagueMapper.toDtoAfichage(league);
    }

    private ResponseDto callApi(String path) {

        try {

            return apiSportClient.get().uri(path)
                    // exécute la requete HTTP et prepare la récupération de la reponse
                    .retrieve()
                    //utilise Jackson ObjectMapper
                    .body(ResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de La'appel API ou du parsing Json", e);
        }
    }


}

