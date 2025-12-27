package org.nowcat.nowcat.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${the-cat-api.api.base-url}")
    private String BASE_URL;

    @Value("${the-cat-api.api.key}")
    private String API_KEY;

    @Bean
    public WebClient theCatApiWebClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("x-api-key", API_KEY)
                .build();
    }
}
