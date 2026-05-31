package com.datafoot.league;


import com.datafoot.exception.entitexception.ExternalApiException;
import com.datafoot.exception.entitexception.LeagueNotFoundException;
import com.datafoot.league.dto.LeagueDto;
import com.datafoot.league.dtoapi.ApiFootballLeagueResponse;
import com.datafoot.league.dtoapi.ApiFootballLeagueItems;
import com.datafoot.league.mapper.LeagueMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;
    private final RestClient apiSportClient;

    public LeagueService(LeagueRepository leagueRepository, RestClient apiSportClient) {
        this.leagueRepository = leagueRepository;
        this.apiSportClient = apiSportClient;
    }


    public List<LeagueDto> findAll() {

        return leagueRepository.findAll().stream().map(LeagueMapper::toDto).toList();
    }

    /**
     * Synchronise une ligue depuis API Football
     * Si la ligue existe déjà en base, ses informations sont mises à jour.
     * Sinon, une nouvelle ligue est créée.
     * @param leagueApiId identifiant de la ligue dans API Football
     * @return la ligue
     * @throws  LeagueNotFoundException si la ligue n'esite pas dans API Football
     * @throws  ExternalApiException si l'appel a l'API externe échoue
     */
    @Transactional
    public LeagueDto importLeagueByApiFootball(int leagueApiId) {
        String path = "/leagues?id=" + leagueApiId;
        ApiFootballLeagueResponse response = callApi(path);

        if (response == null || response.getResponse() == null || response.getResponse().isEmpty()) {


            throw new LeagueNotFoundException("La ligue avec l'id  " + leagueApiId + " API Football " + " n'existe pas");
        }

        ApiFootballLeagueItems apiFootballLeagueItems = response.getResponse().get(0);

        League league = leagueRepository.findByApiFootballLeagueId(leagueApiId).orElseGet(League::new);

        updateLeagueFromApi(league, apiFootballLeagueItems);
        League savedLeague = leagueRepository.save(league);


        return LeagueMapper.toDto(savedLeague);
    }

    private ApiFootballLeagueResponse callApi(String path) {

        try {
            return apiSportClient.get().uri(path)
                    .retrieve()
                    .body(ApiFootballLeagueResponse.class);
        } catch (RestClientException e) {
            throw new ExternalApiException("Erreur lors de L'appel a l'API externe", e);
        }
    }


    private static void updateLeagueFromApi(League league , ApiFootballLeagueItems item){

        league.setApiFootballLeagueId(item.getLeague().getId());
        league.setName(item.getLeague().getName());
        league.setCountry(item.getCountry().getName());
        league.setLogo(item.getLeague().getLogo());
        league.setFlag(item.getCountry().getFlag());
    }
}

