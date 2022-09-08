package com.bestlink.gateway.versionhandler;

import com.alibaba.fastjson2.JSONObject;
import com.bestlink.gateway.config.NacosConfigLocalCatch;
import com.bestlink.gateway.entity.InstanceRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ChuCun JH050169
 * @date 2022/9/6
 */
@Configuration
@SuppressWarnings("unchecked")
public class VersionRouteConfiguration {

    @Autowired
    private NacosConfigLocalCatch nacosConfigLocalCatch;

    public List<InstanceRoute> getInstanceRoutes() {
        List<JSONObject> JsonObjectList = nacosConfigLocalCatch.get("route.json", List.class);
        List<InstanceRoute> instanceRoutes = new ArrayList<>();
        JsonObjectList.forEach(e -> {
            InstanceRoute instanceRoute = JSONObject.parseObject(JSONObject.toJSONString(e), InstanceRoute.class);
            instanceRoutes.add(instanceRoute);
        });
        return instanceRoutes;
    }

}
