package com.bestlink.gateway.versionhandler;

import com.bestlink.gateway.entity.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ChuCun JH050169
 * @date 2022/9/5
 */
@Component
public class VersionHandlerHolder {

    @Autowired
    private ApplicationContext applicationContext;


    private VersionHandler firstVersionHandler;


    private String getServiceInstanceVersion(ServiceInstance serviceInstance) {
        return serviceInstance.getMetadata().get("version");
    }

    public List<ServiceInstance> handleRequest(List<ServiceInstance> serviceInstances, List<Route> routes, HttpHeaders headers) {
        List<ServiceInstance> availableServiceInstances = new ArrayList<>();
        serviceInstances.forEach(serviceInstance -> routes.stream().filter(e -> e.getVersion().equalsIgnoreCase(getServiceInstanceVersion(serviceInstance))).findFirst().
                ifPresent(route -> {
                    if (firstVersionHandler.handleRequest(route, headers)) {
                        availableServiceInstances.add(serviceInstance);
                    }
                }));
        List<ServiceInstance> finalServiceInstanceList = availableServiceInstances.stream().filter(serviceInstance -> {
            if (routes.stream().anyMatch(r -> r.getVersion().equalsIgnoreCase(getServiceInstanceVersion(serviceInstance)))) {
                Route route = routes.stream().filter(r -> r.getVersion().equalsIgnoreCase(getServiceInstanceVersion(serviceInstance))).findFirst().get();
                return !route.getIp().contains("*") || !route.getHeader().contains("*") || !route.getUser().contains("*");
            }
            return true;
        }).collect(Collectors.toList());
        if (!finalServiceInstanceList.isEmpty()) {
            return finalServiceInstanceList;
        }
        return availableServiceInstances;
    }

    @PostConstruct
    public void init() {
        List<VersionHandler> versionHandlers = new ArrayList<>(applicationContext.getBeansOfType(VersionHandler.class).values());
        OrderComparator.sort(versionHandlers);
        if (!versionHandlers.isEmpty()) {
            firstVersionHandler = versionHandlers.get(0);
        }
        for (int i = 0; i < versionHandlers.size(); i++) {
            if (i < versionHandlers.size() - 1) {
                versionHandlers.get(i).setSuccessor(versionHandlers.get(i + 1));
            }
        }
    }


}
