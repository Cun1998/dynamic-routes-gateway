package com.bestlink.gateway.repository.impl;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.bestlink.gateway.entity.InstanceRoute;
import com.bestlink.gateway.entity.Route;
import com.bestlink.gateway.repository.RouteConfigurationRepository;
import com.bestlink.gateway.versionhandler.VersionRouteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ChuCun JH050169
 * @date 2022/9/6
 */
@RefreshScope
@NacosConfigurationProperties(groupId = "BES", dataId = "gatewayDB.yaml", type = ConfigType.YAML, autoRefreshed = true, prefix = "dynamic")
@Component
public class RouteConfigurationRepositoryImpl implements RouteConfigurationRepository {

    //todo 多类型数据源支持
    private String sourceType;

    @Autowired
    private VersionRouteConfiguration versionRouteConfiguration;


    @Override
    public List<Route> getRoutes(String serviceId) {
        List<InstanceRoute> instanceRoutes = versionRouteConfiguration.getInstanceRoutes();
        AtomicReference<List<Route>> atomicReference = new AtomicReference<>();
        instanceRoutes.forEach(instanceRoute -> {
            if (instanceRoute.getInstanceName().equalsIgnoreCase(serviceId)) {
                atomicReference.set(instanceRoute.getRoutes());
            }
        });
        return atomicReference.get();
    }

    @Override
    public boolean shouldRoute(String serviceId) {
        List<InstanceRoute> instanceRoutes = versionRouteConfiguration.getInstanceRoutes();
        return instanceRoutes.stream().map(InstanceRoute::getInstanceName).anyMatch(e -> e.equalsIgnoreCase(serviceId));
    }


}
