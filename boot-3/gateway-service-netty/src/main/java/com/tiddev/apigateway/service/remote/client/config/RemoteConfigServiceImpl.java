package com.tiddev.apigateway.service.remote.client.config;

import com.tiddev.apigateway.service.remote.WebClientAwareService;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Yaser(amin) Sadeghi
 */
@Component
public class RemoteConfigServiceImpl extends WebClientAwareService implements RemoteConfigService {


    public RemoteConfigServiceImpl(LoadBalancedExchangeFilterFunction loadBalancerExchangeFilterFunction, WebClient.Builder webBuilder) {
        super(loadBalancerExchangeFilterFunction, webBuilder);
    }

    @Override
    public Mono<Map<String, String>> getConfig() {
        return getWebClient().get().uri("http://config-service/api/test").retrieve().bodyToMono(new ParameterizedTypeReference<>() {
        });
    }
}
