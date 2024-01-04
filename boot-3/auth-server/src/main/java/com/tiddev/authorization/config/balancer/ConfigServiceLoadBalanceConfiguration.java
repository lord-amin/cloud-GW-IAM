package com.tiddev.authorization.config.balancer;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@LoadBalancerClient(value = "config-service", configuration = ConfigServiceLoadBalancerConfiguration.class)
public class ConfigServiceLoadBalanceConfiguration {

//    @Bean
//    @LoadBalanced
//    public WebClient.Builder loadBalancedWebClientBuilder() {
//        return WebClient.builder();
//    }


}