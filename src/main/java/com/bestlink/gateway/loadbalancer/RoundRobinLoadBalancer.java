package com.bestlink.gateway.loadbalancer;

import com.bestlink.gateway.entity.Route;
import com.bestlink.gateway.repository.impl.RouteConfigurationRepositoryImpl;
import com.bestlink.gateway.utils.SpringContextHolder;
import com.bestlink.gateway.versionhandler.VersionHandlerHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ChuCun JH050169
 * @date 2022/9/6
 */
@Slf4j
public class RoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final AtomicInteger position = new AtomicInteger(new Random().nextInt(1000));
    private final RouteConfigurationRepositoryImpl routeConfigurationRepositoryContext = SpringContextHolder.getBean(RouteConfigurationRepositoryImpl.class);
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final String serviceId;

    private final VersionHandlerHolder versionHandlerHolder = SpringContextHolder.getBean(VersionHandlerHolder.class);

    public RoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        HttpHeaders headers = (HttpHeaders) request.getContext();
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map(list -> getInstanceResponse(list, headers, serviceId));
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, HttpHeaders headers, String serviceId) {
        List<Route> routes = routeConfigurationRepositoryContext.getRoutes(serviceId);
        List<ServiceInstance> serviceInstances = versionHandlerHolder.handleRequest(instances, routes, headers);
        if (serviceInstances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }
        int pos = Math.abs(position.incrementAndGet());
        ServiceInstance instance = serviceInstances.get(pos % serviceInstances.size());
        return new DefaultResponse(instance);
    }
}

