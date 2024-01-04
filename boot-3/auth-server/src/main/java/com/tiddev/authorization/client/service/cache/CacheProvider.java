package com.tiddev.authorization.client.service.cache;

public interface CacheProvider {
    Client get(String clientId);

    Client put(String id, Client client);

    Scope get(Long scopeId);

    Scope put(Long id, Scope scope);

    ScopeList fetch(Long id);

    ScopeList put(Long id, ScopeList scopeList);


    void invalidateClient(String clientId);


    void invalidateScopeList(Long scopeListId);


    void invalidateScope(Long scopeId);


    int clientSize();


    int scopeListSize();


    int scopeSize();


    void invalidateAll();

}
