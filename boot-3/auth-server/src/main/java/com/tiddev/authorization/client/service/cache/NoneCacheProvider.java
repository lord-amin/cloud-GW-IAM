package com.tiddev.authorization.client.service.cache;

public class NoneCacheProvider implements CacheProvider{
    @Override
    public Client get(String clientId) {
        throw new AbstractMethodError();
    }

    @Override
    public Client put(String id, Client client) {
        throw new AbstractMethodError();
    }

    @Override
    public Scope get(Long scopeId) {
        throw new AbstractMethodError();
    }

    @Override
    public Scope put(Long id, Scope scope) {
        throw new AbstractMethodError();
    }

    @Override
    public ScopeList fetch(Long id) {
        throw new AbstractMethodError();
    }

    @Override
    public ScopeList put(Long id, ScopeList scopeList) {
        throw new AbstractMethodError();
    }

    @Override
    public void invalidateClient(String clientId) {
        throw new AbstractMethodError();
    }

    @Override
    public void invalidateScopeList(Long scopeListId) {
        throw new AbstractMethodError();
    }

    @Override
    public void invalidateScope(Long scopeId) {
        throw new AbstractMethodError();
    }

    @Override
    public int clientSize() {
        return -1;
    }

    @Override
    public int scopeListSize() {
        return -1;
    }

    @Override
    public int scopeSize() {
        return -1;
    }

    @Override
    public void invalidateAll() {
        throw new AbstractMethodError();
    }
}
