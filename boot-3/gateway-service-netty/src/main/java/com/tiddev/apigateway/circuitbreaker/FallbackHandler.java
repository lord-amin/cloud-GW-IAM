package com.tiddev.apigateway.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Mono;

@Slf4j
@RestController()
public class FallbackHandler {
    @RequestMapping(value = "/fallback-auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Object> fallbackAuth(ServerWebExchangeDecorator serverWebExchangeDecorator) {
        return fallbackDefault(serverWebExchangeDecorator);
    }

    @RequestMapping(value = "/fallback", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Object> fallbackDefault(ServerWebExchangeDecorator serverWebExchangeDecorator) {
        String originalPath = serverWebExchangeDecorator.getDelegate().getRequest().getURI().getPath();
        Throwable throwable = serverWebExchangeDecorator.getAttributeOrDefault(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR,
                new Exception("Could not found any fallback exception"));
        try {
            if (throwable instanceof CallNotPermittedException) {
                return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(throwable.getMessage()));
            }
            if (throwable instanceof ResponseStatusException) {
                return Mono.just(ResponseEntity.status(((ResponseStatusException) throwable).getStatusCode()).body(throwable.getMessage()));
            }
            return Mono.error(throwable);
        } catch (Exception e) {
            log.warn("Type 2 FallBack happened for {},{}", throwable.getClass(), originalPath);
            log.warn("falling back", throwable);
            return Mono.error(throwable);
        }

    }
}
