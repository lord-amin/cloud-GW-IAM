package com.tiddev.apigateway.service.remote;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Yaser(amin) Sadeghi
 */
@RequiredArgsConstructor
public abstract class WebClientAwareService implements InitializingBean {
    private final LoadBalancedExchangeFilterFunction loadBalancerExchangeFilterFunction;
    private final WebClient.Builder webBuilder;
    @Getter
    private WebClient webClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        webBuilder.filter(loadBalancerExchangeFilterFunction);
        webClient = webBuilder.build();
    }
}
