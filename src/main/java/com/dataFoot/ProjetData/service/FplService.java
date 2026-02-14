package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.FplFixture;
import com.dataFoot.ProjetData.model.FplTeam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FplService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String FIXTURES_URL = "https://fantasy.premierleague.com/api/fixtures/";
    private static final String BOOTSTRAP_URL = "https://fantasy.premierleague.com/api/bootstrap-static/";

    // récupère tous les matchs de la saison
    public List<FplFixture> getFixtures() {
        FplFixture[] fixtures = restTemplate.getForObject(FIXTURES_URL, FplFixture[].class);
        return Arrays.asList(fixtures);
    }

    // récupère toutes les équipes (id + nom)
    public Map<Integer, String> getTeamMap() {
        BootstrapReponse response = restTemplate.getForObject(BOOTSTRAP_URL, BootstrapReponse.class);

        Map<Integer, String> teamMap = new HashMap<>();
        if (response != null && response.getTeams() != null) {
            for (FplTeam team : response.getTeams()) {
                teamMap.put(team.getId(), team.getName());
            }
        }
        return teamMap;
    }
}
