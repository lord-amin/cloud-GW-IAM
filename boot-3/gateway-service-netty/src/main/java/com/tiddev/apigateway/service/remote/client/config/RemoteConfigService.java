package com.tiddev.apigateway.service.remote.client.config;

import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Yaser(amin) Sadeghi
 */
public interface RemoteConfigService {
    Mono<Map<String, String>> getConfig();
}
