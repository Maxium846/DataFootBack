package com.datafoot.league;
import com.datafoot.league.dto.LeagueDto;
import com.datafoot.league.dtoapi.ApiFootballLeagueCountry;
import com.datafoot.league.dtoapi.ApiFootballLeagueResponse;
import com.datafoot.league.dtoapi.ApiFootballLeagueItems;
import com.datafoot.league.dtoapi.ApiFootballLeague;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeagueServiceTest {

    private LeagueRepository leagueRepository;
    private RestClient apiSportClient;
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec requestHeadersSpec;
    private RestClient.ResponseSpec responseSpec;

    private LeagueService leagueService;

    @BeforeEach
    void setUp() {
        leagueRepository = mock(LeagueRepository.class);
        apiSportClient = mock(RestClient.class);
        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        leagueService = new LeagueService(leagueRepository, apiSportClient);
    }

    @Test
    void ReturnAllLeagues() {
        League league = new League();
        league.setId(1L);
        league.setName("Ligue 1");
        league.setCountry("France");
        league.setApiFootballLeagueId(61);
        league.setLogo("logo.png");
        league.setFlag("flag.png");

        when(leagueRepository.findAll()).thenReturn(List.of(league));

        List<LeagueDto> result = leagueService.findAll();

        assertEquals(1, result.size());
        assertEquals("Ligue 1", result.get(0).getName());
        assertEquals("France", result.get(0).getCountry());
        assertEquals(61, result.get(0).getApiFootballId());

        verify(leagueRepository).findAll();
    }

    @Test
    void ImportNewLeagueFromApiFootball() {
        ApiFootballLeagueResponse apiResponse = buildApiResponse();

        when(apiSportClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/leagues?id=61")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ApiFootballLeagueResponse.class)).thenReturn(apiResponse);

        when(leagueRepository.findByApiFootballLeagueId(61))
                .thenReturn(Optional.empty());

        when(leagueRepository.save(any(League.class)))
                .thenAnswer(invocation -> {
                    League league = invocation.getArgument(0);
                    league.setId(1L);
                    return league;
                });

        LeagueDto result = leagueService.importLeagueByApiFootball(61);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ligue 1", result.getName());
        assertEquals("France", result.getCountry());
        assertEquals(61, result.getApiFootballId());

        verify(leagueRepository).findByApiFootballLeagueId(61);
        verify(leagueRepository).save(any(League.class));
    }



    private ApiFootballLeagueResponse buildApiResponse() {
        ApiFootballLeague leagueDto = new ApiFootballLeague();
        leagueDto.setId(61);
        leagueDto.setName("Ligue 1");
        leagueDto.setLogo("logo.png");

        ApiFootballLeagueCountry countryDto = new ApiFootballLeagueCountry();
        countryDto.setName("France");
        countryDto.setFlag("flag.png");

        ApiFootballLeagueItems item = new ApiFootballLeagueItems();
        item.setLeague(leagueDto);
        item.setCountry(countryDto);

        ApiFootballLeagueResponse apiFootballLeagueResponse = new ApiFootballLeagueResponse();
        apiFootballLeagueResponse.setResponse(List.of(item));

        return apiFootballLeagueResponse;
    }
}