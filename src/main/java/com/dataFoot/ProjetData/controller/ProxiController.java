package com.dataFoot.ProjetData.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RestController
@RequestMapping("/api/proxy")
public class ProxiController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/{leagueId}/standings")
    public ResponseEntity<?> getStandings(@PathVariable Long leagueId) {
        try {
            // URL de l'API externe (à adapter si nécessaire)
            String url = "https://api.footballstandings.com/leagues/" + leagueId;

            // Appel de l'API externe
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Retourne directement la réponse de l'API
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du chargement du classement");
        }
    }
    }

