package com.tiddev.apigateway.balancer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

public class ConfigServiceRandomLoadBalancer extends AbstractLoadBalancer {

    public ConfigServiceRandomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                           String serviceId) {
        super(serviceInstanceListSupplierProvider, serviceId);
    }
}