package com.tiddev.apigateway.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.util.List;


//@ConditionalOnProperty(name = "spring.cloud.gateway.redis.enabled", matchIfMissing = true)
@Slf4j
@Configuration
public class RateLimitConfig {
    @Bean
    public RedisRateLimiter myRateLimiter(ReactiveStringRedisTemplate redisTemplate,
                                          @Qualifier(RedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript,
                                          ConfigurationService configurationService) {
        return new MyRedisRateLimiter(redisTemplate, redisScript, configurationService);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> {
            List<String> authorization = exchange.getRequest().getHeaders().get("Authorization");
            if (authorization != null && authorization.size() > 0) {
                log.debug("The authorization found");
                return Mono.just("key://" + exchange.getRequest().getURI().getPath() + "?u=" + authorization);
            }
            return Mono.just("key://" + exchange.getRequest().getURI().getPath());
        };
    }
}
