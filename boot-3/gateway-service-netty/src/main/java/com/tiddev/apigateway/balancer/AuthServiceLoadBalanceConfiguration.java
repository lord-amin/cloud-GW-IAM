package com.tiddev.apigateway.balancer;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@LoadBalancerClient(value = "authorization-service", configuration = AuthServiceLoadBalancerConfiguration.class)
public class AuthServiceLoadBalanceConfiguration {

}