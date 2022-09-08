package com.bestlink.gateway.entity;

import java.util.List;

/**
 * @author ChuCun JH050169
 * @date 2022/9/7
 */
public class InstanceRoute {
    private String instanceName;

    private List<Route> routes;

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
