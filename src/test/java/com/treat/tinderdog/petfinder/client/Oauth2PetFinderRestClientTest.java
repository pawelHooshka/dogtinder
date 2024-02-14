package com.treat.tinderdog.petfinder.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class Oauth2PetFinderRestClientTest {

    private final static String URL = "http://localhost/v2";
    private final static String TOKEN = "some-api-key";

    private Oauth2PetFinderRestClient oauth2PetFinderRestClient;
    @Mock
    private TokenHandler tokenHandler;
    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity> requestCaptor;

    @BeforeEach
    void setUp() {
        this.oauth2PetFinderRestClient = new Oauth2PetFinderRestClient(tokenHandler, restTemplate, URL);
        when(tokenHandler.getAccessToken()).thenReturn(TOKEN);
    }

    @Test
    void testGet() {
        oauth2PetFinderRestClient.get(URL, Object.class);
        verify(restTemplate).exchange(eq(URL), eq(HttpMethod.GET), requestCaptor.capture(), eq(Object.class));
        final HttpEntity<Void> request = requestCaptor.getValue();
        final HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("Authorization", "Bearer some-api-key");
        expectedHeaders.add("Accept", "*/*");
        expectedHeaders.add("Content-Type", "application/json");
        assertThat(request.getHeaders(), is(expectedHeaders));
    }

    @Test
    void testGetWithUri() {
        oauth2PetFinderRestClient.get("/animals", Object.class);
        verify(restTemplate)
                .exchange(
                        eq(URL.concat("/animals")), eq(HttpMethod.GET), requestCaptor.capture(), eq(Object.class));
        final HttpEntity<Void> request = requestCaptor.getValue();
        final HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("Authorization", "Bearer some-api-key");
        expectedHeaders.add("Accept", "*/*");
        expectedHeaders.add("Content-Type", "application/json");
        assertThat(request.getHeaders(), is(expectedHeaders));
    }

    @Test
    void testGetWithCustomHeaders() {
        final Map<String, Object> queryParameters = new LinkedHashMap<>();
        queryParameters.put("param1", "value1");
        queryParameters.put("param2", "value2");
        oauth2PetFinderRestClient.get("/animals", queryParameters, Object.class);
        verify(restTemplate).exchange(
                eq(URL.concat("/animals?param1=value1&param2=value2")),
                eq(HttpMethod.GET),
                requestCaptor.capture(),
                eq(Object.class));
        final HttpEntity<Void> request = requestCaptor.getValue();
        final HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("Authorization", "Bearer some-api-key");
        expectedHeaders.add("Accept", "*/*");
        expectedHeaders.add("Content-Type", "application/json");
        assertThat(request.getHeaders(), is(expectedHeaders));
    }
}
