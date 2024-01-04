package com.tiddev.authorization.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiddev.authorization.client.domain.ClientDetailRepository;
import com.tiddev.authorization.client.domain.ClientDetailsEntity;
import com.tiddev.authorization.client.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenFormat;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ConfigurationSettingNames;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@EnableWebSecurity
public class SecurityConfig {
    /**
     * First will be applied the OAuth2 security filters configuration.
     * In this configuration, I only indicate that all the failing request will be redirected to the /login page.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.build();
    }

    //    curl -iX POST http://192.168.102.82:8092/oauth2/token -d "client_id=gateway-client-id&client_secret=123456789&grant_type=client_credentials"
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate,ClientService clientService) {
//                RegisteredClient.withId(UUID.randomUUID().toString())
//                        .clientId("1")
//                        .clientSecret("1")
//                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
//                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                        .scope(OidcScopes.OPENID).build());

        //        jdbcRegisteredClientRepository.save(registeredClient);
        return new JdbcRegisteredClientRepository(jdbcTemplate);
//        return inMemoryRegisteredClientRepository;
    }



    /**
     * Acceptable URL of the authorization server
     */
    @Bean
    public ProviderSettings providerSettings(@Value("${provider.url}") String pUrl) {
        ProviderSettings build = ProviderSettings.builder()
                .issuer(pUrl)
                .build();
        build.getSettings().forEach((key, value) -> log.warn("===================>  {} = {}", key, value));
        return build;
    }

}
