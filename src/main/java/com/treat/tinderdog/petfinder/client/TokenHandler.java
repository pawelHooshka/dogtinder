package com.treat.tinderdog.petfinder.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Component
public class TokenHandler {

    private final String appName;
    private final String clientId;
    private final OAuth2AuthorizedClientManager oauth2ClientManager;

    public TokenHandler(
            final OAuth2AuthorizedClientManager oauth2ClientManager,
            @Value("${petfinder.client.registration.name}")
            final String appName,
            @Value("${spring.security.oauth2.client.registration.app.client-id}")
            final String clientId) {
        this.oauth2ClientManager = oauth2ClientManager;
        this.appName = appName;
        this.clientId = clientId;
    }

    public String getAccessToken() {
        final OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(appName)
                .principal(clientId)
                .build();

        final OAuth2AuthorizedClient authorizedClient = oauth2ClientManager.authorize(authorizeRequest);
        final OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        return accessToken.getTokenValue();
    }
}
