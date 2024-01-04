package com.tiddev.authorization.client.service;


import com.tiddev.authorization.client.domain.client.ClientDetailRepository;
import com.tiddev.authorization.client.domain.client.ClientDetailsEntity;
import com.tiddev.authorization.client.service.cache.CacheProvider;
import com.tiddev.authorization.client.service.cache.Client;
import com.tiddev.authorization.client.service.cache.Scope;
import com.tiddev.authorization.client.service.cache.ScopeList;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Slf4j
public class SimpleInMemoryJdbcRegisteredClientRepository extends MyJdbcRegisteredClientRepository {
    private final CacheProvider cacheProvider;

    public SimpleInMemoryJdbcRegisteredClientRepository(JdbcOperations jdbcOperations, ClientDetailRepository repository, CacheProvider cacheProvider) {
        super(jdbcOperations, repository);
        this.cacheProvider = cacheProvider;
        log.warn("Client repository initialized ");
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        log.info("find client {} with multiple join", clientId);
        Client cachedClient = cacheProvider.get(clientId);
        boolean needQueryDB = isNeedQueryDB(cachedClient);
        if (!needQueryDB) {
            log.info("Find client in RAM {}", clientId);
            return toRegisteredClient(cachedClient);
        }
        log.info("Find client in DB {}", clientId);
        Optional<ClientDetailsEntity> clientDetailsEntity = getRepository().findByClientId(clientId);
        if (clientDetailsEntity.isEmpty())
            throw new OAuth2AuthenticationException(new OAuth2Error("100", "The client " + clientId + " not found", ""));
        ClientDetailsEntity entity = clientDetailsEntity.get();
        if (!entity.getEnable()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("100", "The client " + clientId + " disabled", ""));
        }
        List<Tuple> scopeListEntities = getRepository().findByClientPkId(entity.getId());
        if (scopeListEntities.isEmpty())
            throw new OAuth2AuthenticationException(new OAuth2Error("100", "The client " + clientId + " not authorized", ""));
        Client client = updateClientCache(clientId, entity.getId(), entity.getClientSecret(), entity.getAccessTokenExpireSeconds());
        client.setScopeListId(new HashSet<>());
        for (Tuple tuple : scopeListEntities) {
            Long scopeListId = tuple.get(0, Long.class);
            Long scopeId = tuple.get(1, Long.class);
            String scopeName = tuple.get(2, String.class);
            String url = tuple.get(3, String.class);
            client.getScopeListId().add(scopeListId);
            ScopeList scopeList = cacheProvider.fetch(scopeListId);
            if (scopeList == null) {
                scopeList = new ScopeList();
                cacheProvider.put(scopeListId, scopeList);
            }
            scopeList.setId(scopeListId);
            scopeList.getScopeId().add(scopeId);
            Scope scope = cacheProvider.get(scopeId);
            if (scope == null) {
                scope = new Scope();
                cacheProvider.put(scopeId, scope);
            }
            scope.setId(scopeId);
            scope.setName(scopeName);
            scope.setUrlPattern(url);

        }
        Client clientRequest = cacheProvider.get(clientId);
        if (clientRequest == null)
            throw new OAuth2AuthenticationException(new OAuth2Error("100", "The client " + clientId + " not found in cache", ""));
        return toRegisteredClient(clientRequest);

    }

    private Client updateClientCache(String clientId, Long cPkId, String cSecret, Long accessTokenSec) {
        Client client = cacheProvider.get(clientId);
        if (client == null) {
            client = new Client();
            cacheProvider.put(clientId, client);
        }
        client.setId(cPkId);
        client.setClientId(clientId);
        client.setClientSecret(cSecret);
        client.setAccessTokenSeconds(accessTokenSec);
        return client;
    }

    private boolean isNeedQueryDB(Client cachedClient) {
        if (cachedClient == null) {
            return true;
        } else {
            for (Long scopeListId : cachedClient.scopeListId) {
                ScopeList scopeList = cacheProvider.fetch(scopeListId);
                if (scopeList == null) {
                    return true;
                }
                for (Long scopeId : scopeList.getScopeId()) {
                    Scope scope = cacheProvider.get(scopeId);
                    if (scope == null) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private RegisteredClient toRegisteredClient(Client clientRequest) {
        return RegisteredClient.withId(clientRequest.getId() + "")
                .clientId(clientRequest.getClientId())
                .clientSecret(clientRequest.getClientSecret())
                .clientName(clientRequest.getClientId())
                .clientAuthenticationMethods(clientAuthenticationMethods -> clientAuthenticationMethods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST))
                .authorizationGrantTypes(authorizationGrantTypes -> authorizationGrantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS))
                .scopes(scopes -> {
                    Set<Long> scopeListId = clientRequest.getScopeListId();
                    for (Long aLong : scopeListId) {
                        ScopeList scopeList = cacheProvider.fetch(aLong);
                        for (Long scopeId : scopeList.getScopeId()) {
                            Scope scope = cacheProvider.get(scopeId);
                            scopes.add(scope.getName());
                        }
                    }
                })
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.of(clientRequest.getAccessTokenSeconds(), ChronoUnit.SECONDS))
                        .refreshTokenTimeToLive(Duration.of(clientRequest.getAccessTokenSeconds() + 5, ChronoUnit.SECONDS))
                        .reuseRefreshTokens(true)
                        .build()).build();
    }
}
