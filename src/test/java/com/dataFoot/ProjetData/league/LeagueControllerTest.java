package com.dataFoot.ProjetData.league;

import com.dataFoot.ProjetData.exception.GlobalExceptionHandler;
import com.dataFoot.ProjetData.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.ProjetData.league.dto.LeagueByIdDto;
import com.dataFoot.ProjetData.security.JwtFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LeagueController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
})
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class LeagueControllerTest {



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeagueService leagueService;

    @Test
    void testGetLeagueById_ok() throws Exception {
        LeagueByIdDto dto = new LeagueByIdDto();
        dto.setId(1L);
        dto.setName("Premier League");

        Mockito.when(leagueService.getLeagueById(1L)).thenReturn(dto);


        mockMvc.perform(get("/api/leagues/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Premier League"));
    }

    @Test
    void testGetLeagueById_notFound() throws Exception {
        Mockito.when(leagueService.getLeagueById(1L))
                .thenThrow(new LeagueNotFoundException("Cette ligue n'existe pas"));

        mockMvc.perform(get("/leagues/1"))
                .andExpect(status().isNotFound());
    }
}

