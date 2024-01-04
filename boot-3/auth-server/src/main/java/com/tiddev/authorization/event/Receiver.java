package com.tiddev.authorization.event;

import com.tiddev.authorization.client.service.cache.CacheProvider;
import com.tiddev.authorization.client.service.cache.HeapCacheProvider;
import com.tiddev.authorization.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;
@ConditionalOnProperty(name = "cache.model", havingValue = "heap")
@Slf4j
@RequiredArgsConstructor
@Component
public class Receiver {
    private HeapCacheProvider cacheProvider;

    private final AppConfig appConfig;

    @RabbitListener(queues = "#{userRabbitConfig.eventQName}")
    public void listen(ReloadEvent message) {
        log.info("The producer of message is {}", message.getEventProducerId());
        if (appConfig.getSpring().getInstanceId().equals(message.getEventProducerId())) {
            log.warn("The event ignored {}", message);
            return;
        }
        if (Objects.nonNull(message.getClientId())) {
            cacheProvider.invalidateClientNoEvent(message.getClientId());
        }
        if (Objects.nonNull(message.getScopeId())) {
            cacheProvider.invalidateScopeNoEvent(message.getScopeId());
        }
        if (Objects.nonNull(message.getScopeListId())) {
            cacheProvider.invalidateScopeListNoEvent(message.getScopeListId());
        }
    }

    @Autowired
    public void setCacheProvider(CacheProvider cacheProvider) {
        this.cacheProvider = (HeapCacheProvider) cacheProvider;
    }
}