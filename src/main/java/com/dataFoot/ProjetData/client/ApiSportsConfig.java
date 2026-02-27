package com.dataFoot.ProjetData.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiSportsConfig {

    @Bean
    RestClient apiSportsClient(
            @Value("${apisports.baseUrl}") String baseUrl,
            @Value("${apisports.key}") String apiKey
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-apisports-key", apiKey)
                .build();
    }
}
