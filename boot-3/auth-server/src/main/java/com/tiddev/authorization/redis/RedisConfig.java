package com.tiddev.authorization.redis;

import com.tiddev.authorization.event.Receiver;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "cache.model", havingValue = "reids")
public class RedisConfig {
    public RedisConfig() {
    }
//
//    @Bean
//    Queue queue() {
//        return new Queue(QUEUE_NAME, true);
//    }


}
