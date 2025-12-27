package org.nowcat.nowcat.global.infra.theCatApi;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.global.infra.theCatApi.dto.TheCatApiResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TheCatApiClient {

    private final WebClient theCatApiClient;

    public String getRandomCatImageUrl() {
        final List<TheCatApiResponseDto> response = theCatApiClient.get()
                .uri(
                        uriBuilder -> uriBuilder
                        .path("/v1/images/search")
                        .queryParam("mime_types", "jpg,png")
                        .queryParam("format", "json")
                        .queryParam("order", "RANDOM")
                        .queryParam("limit", "1")
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TheCatApiResponseDto>>(){})
                .block();

        if(response == null || response.isEmpty()) {
            return null;
        }

        return response.getFirst().getUrl();
    }
}
