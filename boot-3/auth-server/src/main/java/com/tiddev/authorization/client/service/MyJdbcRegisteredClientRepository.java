package com.tiddev.authorization.client.service;


import com.tiddev.authorization.client.domain.client.ClientDetailRepository;
import com.tiddev.authorization.client.domain.client.ClientDetailsEntity;
import com.tiddev.authorization.client.domain.scope.ScopeEntity;
import com.tiddev.authorization.client.domain.scopeList.ScopeListEntity;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Slf4j
public class MyJdbcRegisteredClientRepository extends JdbcRegisteredClientRepository {
    private final ClientDetailRepository repository;

    public MyJdbcRegisteredClientRepository(JdbcOperations jdbcOperations, ClientDetailRepository repository) {
        super(jdbcOperations);
        this.repository = repository;
    }

    @Override
    public RegisteredClient findById(String id) {
        throw new AbstractMethodError();
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        log.info("find client {} with multiple join", clientId);
        List<Tuple> byClientId = getRepository().fastFindByClientId(clientId);
        ClientDetailsEntity c = new ClientDetailsEntity();
        c.setId(byClientId.get(0).get(0, Long.class));
        c.setClientSecret(byClientId.get(0).get(2, String.class));
        c.setClientId(byClientId.get(0).get(1, String.class));
        c.setClientName(byClientId.get(0).get(1, String.class));
        c.setAuthorizationGrantTypes(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
        c.setClientAuthenticationMethods(ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue());
        c.setAccessTokenExpireSeconds(5000L);
        c.setRefreshTokenExpireSeconds(6000L);
        c.setScopes(new ArrayList<>());
        c.getScopes().add(new ScopeListEntity(-1L));
        c.getScopes().get(0).setScopes(new ArrayList<>());
        byClientId.forEach(tuple -> {
            ScopeEntity e = new ScopeEntity(1L);
            c.getScopes().get(0).getScopes().add(e);
            e.setName(tuple.get(3, String.class));
            e.setUrlPattern(tuple.get(4, String.class));
        });

        return toRegisteredClient(c);
    }

    private static RegisteredClient toRegisteredClient(ClientDetailsEntity clientRequest) {
        Function<ScopeListEntity, Stream<? extends String>> scopeListEntityStreamFunction = scopeListEntity -> scopeListEntity.getScopes().stream().map(ScopeEntity::getName);
        return RegisteredClient.withId(clientRequest.getId() + "")
                .clientId(clientRequest.getClientId())
                .clientSecret(clientRequest.getClientSecret())
                .clientName(clientRequest.getClientName())
                .clientAuthenticationMethods(clientAuthenticationMethods -> clientAuthenticationMethods.addAll(
                        StringUtils.commaDelimitedListToSet(clientRequest
                                .getClientAuthenticationMethods()).stream().map(ClientAuthenticationMethod::new).toList()))
                .authorizationGrantTypes(authorizationGrantTypes -> authorizationGrantTypes.addAll(
                        StringUtils.commaDelimitedListToSet(clientRequest.getAuthorizationGrantTypes()).stream().map(AuthorizationGrantType::new).toList()))
                .scopes(scopes -> scopes.addAll(clientRequest.getScopes().stream().flatMap(scopeListEntityStreamFunction).toList()))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.of(clientRequest.getAccessTokenExpireSeconds(), ChronoUnit.SECONDS))
                        .refreshTokenTimeToLive(Duration.of(clientRequest.getRefreshTokenExpireSeconds() + 5, ChronoUnit.SECONDS))
                        .reuseRefreshTokens(true)
                        .build()).build();
    }


    @Transactional()
    @Override
    public void save(RegisteredClient registeredClient) {
        throw new AbstractMethodError();
    }

    public ClientDetailRepository getRepository() {
        return repository;
    }
}
