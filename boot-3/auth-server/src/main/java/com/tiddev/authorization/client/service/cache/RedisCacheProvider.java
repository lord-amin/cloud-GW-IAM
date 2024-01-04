package com.tiddev.authorization.client.service.cache;

import com.tiddev.authorization.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RedisCacheProvider implements CacheProvider {
    private final AppConfig appConfig;

    @Override
    public Client get(String clientId) {
        return null;
    }

    @Override
    public Client put(String id, Client client) {
        return null;
    }

    @Override
    public Scope get(Long scopeId) {
        return null;
    }

    @Override
    public Scope put(Long id, Scope scope) {
        return null;
    }

    @Override
    public ScopeList fetch(Long id) {
        return null;
    }

    @Override
    public ScopeList put(Long id, ScopeList scopeList) {
        return null;
    }

    @Override
    public void invalidateClient(String clientId) {

    }

    @Override
    public void invalidateScopeList(Long scopeListId) {

    }

    @Override
    public void invalidateScope(Long scopeId) {

    }

    @Override
    public int clientSize() {
        return 0;
    }

    @Override
    public int scopeListSize() {
        return 0;
    }

    @Override
    public int scopeSize() {
        return 0;
    }

    @Override
    public void invalidateAll() {

    }
}
