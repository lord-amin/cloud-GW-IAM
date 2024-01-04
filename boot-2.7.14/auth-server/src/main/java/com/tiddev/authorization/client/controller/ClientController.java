package com.tiddev.authorization.client.controller;

import com.tiddev.authorization.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ClientController {
    private final RegisteredClientRepository registeredClientRepository;
    private final ClientService clientService;

    @PostMapping("/register/{clientId}")
    public String register(@PathVariable("clientId") String clientId) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(clientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope(OidcScopes.OPENID)
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.of(60L, ChronoUnit.SECONDS)).build()).build();
        registeredClientRepository.save(registeredClient);
        return registeredClient.getId();
    }

    @PostMapping("/single-file-upload")
    public String handleFileUploadUsingCurl(
            @RequestParam("file") MultipartFile file) throws IOException {
        return "total is " + clientService.saveLocations(new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)));
    }
}
