package com.tiddev.authorization.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "cache.model", havingValue = "heap")
@Service
@RequiredArgsConstructor
public class RabbitTemplateDecorator {
    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;

    public void convertAndSend(ReloadEvent object) {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", object);
    }

}
