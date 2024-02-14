package com.treat.tinderdog.petfinder.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class Oauth2PetFinderRestClient {

    private final TokenHandler tokenHandler;
    private final RestTemplate restTemplate;

    private String petFinderBaseUrl;

    public Oauth2PetFinderRestClient(final TokenHandler tokenHandler,
                                     final RestTemplate restTemplate,
                                     @Value("${petfinder.client.api.url}")
                                     final String petFinderBaseUrl) {
        this.tokenHandler = tokenHandler;
        this.restTemplate = restTemplate;
        this.petFinderBaseUrl = petFinderBaseUrl;
    }

    public <T> ResponseEntity<T> get(final String uri,
                                     final Map<String, Object> queryParams,
                                     final Class<T> responseType){
        final UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(petFinderBaseUrl.concat(uri));
        queryParams.keySet()
                .forEach(queryParam -> uriBuilder.queryParam(queryParam, queryParams.get(queryParam)));
        return get(uriBuilder.toUriString(), responseType);
    }

    public <T> ResponseEntity<T> get(String url, final Class<T> responseType) {
        url = toUrl(url);
        final String token = tokenHandler.getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Accept", "*/*");
        headers.add("Content-Type", "application/json");
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, responseType);
    }

    private String toUrl(final String url) {
        return url.startsWith("/")
                ? petFinderBaseUrl.concat(url)
                : url;
    }
}
