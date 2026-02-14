package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.model.Match;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class FplController {

    private String fetchUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            content.append(line);
        }
        in.close();
        conn.disconnect();
        return content.toString();
    }

    @GetMapping("/api/fpl/fixtures")
    public ResponseEntity<String> getFixtures() throws IOException {
        String fixturesJson = fetchUrl("https://fantasy.premierleague.com/api/fixtures/");
        return ResponseEntity.ok(fixturesJson);
    }

    @GetMapping("/api/fpl/teams")
    public ResponseEntity<String> getTeams() throws IOException {
        String teamsJson = fetchUrl("https://fantasy.premierleague.com/api/bootstrap-static/");
        return ResponseEntity.ok(teamsJson);
    }



}
