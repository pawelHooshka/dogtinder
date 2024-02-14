package com.treat.tinderdog.petfinder.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

@ExtendWith(MockitoExtension.class)
public class TokenHandlerTest {

    private  static final String APP_NAME = "app";
    private static final  String CLIENT_ID = "client-id";
    private static final String TOKEN = "some-api-key";

    private TokenHandler tokenHandler;
    @Mock
    private OAuth2AuthorizedClientManager oauth2ClientManager;
    @Mock
    private OAuth2AuthorizedClient authorizedClient;
    @Mock
    private OAuth2AccessToken accessToken;

    @BeforeEach
    void setUp() {
        this.tokenHandler = new TokenHandler(oauth2ClientManager, APP_NAME, CLIENT_ID);
    }

    @Test
    void testGetAccessToken() {
        when(oauth2ClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(authorizedClient);
        when(authorizedClient.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn(TOKEN);
        assertThat(tokenHandler.getAccessToken(), is(TOKEN));
    }
}
