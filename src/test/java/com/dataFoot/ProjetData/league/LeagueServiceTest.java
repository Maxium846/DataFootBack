package com.dataFoot.ProjetData.league;

import com.dataFoot.ProjetData.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.ProjetData.league.dto.LeagueByIdDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LeagueServiceTest {

    @Mock
    private LeagueRepository leagueRepository;

    @InjectMocks
    private LeagueService leagueService;

    @Test
    void testGetLeagueById_ok() {
        League league = new League();
        league.setId(1L);
        league.setName("Premier League");

        Mockito.when(leagueRepository.findById(1L)).thenReturn(Optional.of(league));

        LeagueByIdDto dto = leagueService.getLeagueById(1L);

        Assertions.assertEquals("Premier League", dto.getName());
    }

    @Test
    void testGetLeagueById_notFound() {
        Mockito.when(leagueRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(LeagueNotFoundException.class, () -> {
            leagueService.getLeagueById(1L);
        });
    }
}
