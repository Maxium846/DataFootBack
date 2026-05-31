package com.datafoot.league;

import com.datafoot.league.dto.LeagueDto;
import com.datafoot.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeagueController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeagueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeagueService leagueService;
    @MockBean
    private JwtService jwtService;

    @Test
    void ReturnAllLeagues() throws Exception {
        LeagueDto ligue1 = new LeagueDto(
                1L,
                "Ligue 1",
                "France",
                1,
                "logo",
                "flag"

        );

        LeagueDto premierLeague = new LeagueDto(
                2L,
                "Premier League",
                "England",
                1,
                "logo",
                "flag"
        );

        when(leagueService.findAll()).thenReturn(List.of(ligue1, premierLeague));

        mockMvc.perform(get("/api/leagues"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ligue 1"))
                .andExpect(jsonPath("$[0].country").value("France"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Premier League"))
                .andExpect(jsonPath("$[1].country").value("England"));
    }

    @Test
    void ImportLeagueByApiFootballId() throws Exception {
        LeagueDto ligue1 = new LeagueDto(
                1L,
                "Ligue 1",
                "France",
                1,
                "https://flag-france.png",
                "flag"
        );

        when(leagueService.importLeagueByApiFootball(61)).thenReturn(ligue1);

        mockMvc.perform(post("/api/leagues/import/61"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ligue 1"))
                .andExpect(jsonPath("$.country").value("France"));
    }
}