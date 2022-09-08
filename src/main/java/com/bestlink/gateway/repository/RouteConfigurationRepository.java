package com.bestlink.gateway.repository;

import com.bestlink.gateway.entity.Route;

import java.util.List;

/**
 * @author ChuCun JH050169
 * @date 2022/9/6
 */
public interface RouteConfigurationRepository {

    List<Route> getRoutes(String serviceId);

    boolean shouldRoute(String serviceId);
}
