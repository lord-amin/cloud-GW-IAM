package com.tiddev.authorization.config.balancer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

public class ConfigServiceRandomLoadBalancer extends AbstractLoadBalancer {

    public ConfigServiceRandomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                           String serviceId) {
        super(serviceInstanceListSupplierProvider, serviceId);
    }

    public ConfigServiceRandomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                           String serviceId, int seedPosition) {
        super(serviceInstanceListSupplierProvider, serviceId, seedPosition);
    }
}