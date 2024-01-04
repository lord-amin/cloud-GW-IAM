package com.tiddev.authorization.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor
@Configuration
@ConditionalOnProperty(name = "cache.model", havingValue = "heap")
@ConfigurationProperties(prefix = "rabbit.user")
public class UserRabbitConfig implements InitializingBean {
    @Getter
    @Setter
    public String eventExchangeName;
    @Getter
    @Setter
    private String eventQName;

    @Bean
    Queue queue() {
        return new Queue(eventQName, true);
    }

    @Bean
    FanoutExchange exchange() {
        return new FanoutExchange(eventExchangeName);
    }

    @Bean
    Binding binding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }


    @Override
    public void afterPropertiesSet() {
        //        todo: this property is required , but safer way is use pattern
        System.setProperty("spring.amqp.deserialization.trust.all", "true");
    }
}
