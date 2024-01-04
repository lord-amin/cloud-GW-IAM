package com.tiddev.authorization.client.service.cache;

import com.tiddev.authorization.config.AppConfig;
import com.tiddev.authorization.event.RabbitTemplateDecorator;
import com.tiddev.authorization.event.ReloadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class HeapCacheProvider implements CacheProvider {
    private final Map<String, Client> clients = new HashMap<>();
    private final Map<Long, ScopeList> scopeList = new HashMap<>();
    private final Map<Long, Scope> scopes = new HashMap<>();
    private final RabbitTemplateDecorator rabbitTemplate;
    private final AppConfig appConfig;

    @Override
    public Client get(String id) {
        return this.clients.get(id);
    }

    @Override
    public Client put(String id, Client client) {
        return this.clients.put(id, client);
    }

    @Override
    public Scope get(Long id) {
        return this.scopes.get(id);
    }

    @Override
    public Scope put(Long id, Scope scope) {
        return this.scopes.put(id, scope);
    }

    @Override
    public ScopeList fetch(Long id) {
        return this.scopeList.get(id);
    }

    @Override
    public ScopeList put(Long id, ScopeList scopeList) {
        return this.scopeList.put(id, scopeList);
    }

    @Override
    public void invalidateClient(String clientId) {
        log.info("The client {} removed from cache and send event to all", clientId);
        clients.remove(clientId);
        sendEvent(reloadEvent -> reloadEvent.setClientId(clientId));
    }

    @Override
    public void invalidateScopeList(Long scopeListId) {
        log.info("The scopeList {} removed from cache and send event to all", scopeListId);
        scopeList.remove(scopeListId);
        sendEvent(reloadEvent -> reloadEvent.setScopeListId(scopeListId));
    }

    @Override
    public void invalidateScope(Long scopeId) {
        log.info("The scope {} removed from cache and send event to all", scopeId);
        scopes.remove(scopeId);
        sendEvent(reloadEvent -> reloadEvent.setScopeId(scopeId));
    }

    public void invalidateScopeNoEvent(Long scopeId) {
        log.info("The scope {} removed from cache", scopeId);
        scopes.remove(scopeId);
    }

    public void invalidateScopeListNoEvent(Long scopeListId) {
        log.info("The scopeList {} removed from cache", scopeListId);
        scopeList.remove(scopeListId);
    }

    public void invalidateClientNoEvent(String clientId) {
        log.info("The client {} removed from cache", clientId);
        clients.remove(clientId);
    }

    @Override
    public int clientSize() {
        return this.clients.size();
    }

    @Override
    public int scopeListSize() {
        return this.scopeList.size();
    }

    @Override
    public int scopeSize() {
        return this.scopes.size();
    }

    @Override
    public void invalidateAll() {
        this.clients.clear();
        this.scopeList.clear();
        this.scopes.clear();
    }

    private void sendEvent(Consumer<ReloadEvent> consumer) {
        ReloadEvent object = new ReloadEvent(appConfig.getSpring().getInstanceId());
        consumer.accept(object);
        rabbitTemplate.convertAndSend(object);
    }
}
