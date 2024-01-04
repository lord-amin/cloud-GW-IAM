package com.tiddev.authorization.client.service;


import com.tiddev.authorization.client.domain.client.ClientDetailRepository;
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
import java.util.List;
import java.util.Set;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Slf4j
public class AdvancedInMemoryJdbcRegisteredClientRepository extends MyJdbcRegisteredClientRepository {
    private final CacheProvider cacheProvider;

    public AdvancedInMemoryJdbcRegisteredClientRepository(JdbcOperations jdbcOperations, ClientDetailRepository repository, CacheProvider cacheProvider) {
        super(jdbcOperations, repository);
        this.cacheProvider = cacheProvider;
        log.error("Client repository initialized ");
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        log.info("find client {} with multiple join", clientId);
        boolean needQueryDB = false;
        Client cachedClient = cacheProvider.get(clientId);
        needQueryDB = isNeedQueryDB(needQueryDB, cachedClient);
        if (!needQueryDB) {
            log.info("Find client in RAM {}", clientId);
            return toRegisteredClient(cachedClient);
        }
        log.info("Find client in DB {}", clientId);
        List<Tuple> byClientIdList = getRepository().fastFindByClientId(clientId);
        if (byClientIdList.isEmpty()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("100", "The client " + clientId + " not found or not authorized", ""));
        }
        for (Tuple byClientId : byClientIdList) {
            updateCache(byClientId);
        }
        return toRegisteredClient(cacheProvider.get(clientId));

    }

    private void updateCache(Tuple byClientId) {
        Long clientPkId = byClientId.get(0, Long.class);
        String clientId = byClientId.get(1, String.class);
        String clientSecret = byClientId.get(2, String.class);
        String scopeName = byClientId.get(3, String.class);
        String scopeUrl = byClientId.get(4, String.class);
        Long scopeId = byClientId.get(5, Long.class);
        Long scopeListId = byClientId.get(6, Long.class);
        Long accessTokenSec = byClientId.get(7, Long.class);
        Boolean enabled = byClientId.get(8, Boolean.class);
        if (!enabled)
            throw new OAuth2AuthenticationException(new OAuth2Error("100", "The client " + clientId + " disabled", ""));
        updateClientCache(clientId, clientPkId, clientSecret, scopeListId, accessTokenSec);
        updateScopeListCache(scopeId, scopeListId);
        updateScopeCache(scopeName, scopeUrl, scopeId);
    }

    private void updateScopeCache(String scopeName, String scopeUrl, Long scopeId) {
        Scope scope = cacheProvider.get(scopeId);
        if (scope == null) {
            scope = new Scope();
            cacheProvider.put(scopeId, scope);
        }
        scope.setId(scopeId);
        scope.setName(scopeName);
        scope.setUrlPattern(scopeUrl);
    }

    private void updateScopeListCache(Long scopeId, Long scopeListId) {
        ScopeList scopeList = cacheProvider.fetch(scopeListId);
        if (scopeList == null) {
            scopeList = new ScopeList();
            cacheProvider.put(scopeListId, scopeList);
        }
        scopeList.setId(scopeListId);
        scopeList.getScopeId().add(scopeId);
    }

    private void updateClientCache(String clientId, Long cPkId, String cSecret, Long scopeListId, Long accessTokenSec) {
        Client client = cacheProvider.get(clientId);
        if (client == null) {
            client = new Client();
            cacheProvider.put(clientId, client);
        }
        client.setId(cPkId);
        client.setClientId(clientId);
        client.setClientSecret(cSecret);
        client.setAccessTokenSeconds(accessTokenSec);
        client.getScopeListId().add(scopeListId);
    }

    private boolean isNeedQueryDB(boolean needQueryDB, Client cachedClient) {
        if (cachedClient == null) {
            needQueryDB = true;
        } else {
            for (Long scopeListId : cachedClient.scopeListId) {
                if (needQueryDB)
                    break;
                ScopeList scopeList = cacheProvider.fetch(scopeListId);
                if (scopeList == null) {
                    needQueryDB = true;
                    break;
                }
                for (Long scopeId : scopeList.getScopeId()) {
                    Scope scope = cacheProvider.get(scopeId);
                    if (scope == null) {
                        needQueryDB = true;
                        break;
                    }
                }

            }
        }
        return needQueryDB;
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
