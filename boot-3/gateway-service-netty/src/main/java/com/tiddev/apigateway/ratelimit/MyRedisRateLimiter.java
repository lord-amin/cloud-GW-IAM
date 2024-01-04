
package com.tiddev.apigateway.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public class MyRedisRateLimiter extends RedisRateLimiter {
    private final ReactiveStringRedisTemplate redisTemplate;

    public MyRedisRateLimiter(ReactiveStringRedisTemplate redisTemplate,
                              RedisScript<List<Long>> script,
                              ConfigurationService configurationService) {
        super(redisTemplate, script, configurationService);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        Config routeConfig = loadConfiguration(routeId);

        int rateSeconds = routeConfig.getReplenishRate();
        int capacity = routeConfig.getBurstCapacity();
        return this.redisTemplate.opsForValue().increment(id, 1).flatMap(current -> {
            if (current == null) {
                return Mono.just(new Response(true, getHeaders(routeConfig, -1L)));
            }
            if (current.equals(1L)) {
                return redisTemplate.expire(id, Duration.of(rateSeconds, ChronoUnit.SECONDS)).flatMap(aBoolean -> {
                    log.debug("SUCCESS. first time . Setting expire for {} to {} seconds", id, rateSeconds);
                    return Mono.just(new Response(true, getHeaders(routeConfig, -1L)));
                });
            }
            if (capacity - current < 0) {
                log.debug("FAILED .rate limit is OPEN for {}. (cpt={},curr={})", id, capacity, current);
                return Mono.error(new TooManyRequestsException());
            }
            log.debug("SUCCESS .rate limit is CLOSED for {}. (cpt={},curr={})", id, capacity, current);
            return Mono.just(new Response(true, getHeaders(routeConfig, -1L)));
        });
    }

    Config loadConfiguration(String routeId) {
        Config routeConfig = getConfig().get(routeId);

        if (routeConfig == null) {
            routeConfig = getConfig().get(RouteDefinitionRouteLocator.DEFAULT_FILTERS);
        }

        if (routeConfig == null) {
            throw new IllegalArgumentException("No Configuration found for route " + routeId + " or defaultFilters");
        }
        return routeConfig;
    }
}
