package com.bestlink.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ChuCun JH050169
 * @date 2022/9/6
 */

@Configuration
public class NacosConfig {
    /**
     * 前提条件 要求  serverAddr、namespace、group使用bootstrap.yml 中 spring cloud nacos config 的。
     */
    @Bean
    public Map<String, Class<?>> nacosConfigLocalCacheInfoMap() {
        // key:dataId, value:对应数据类型
        Map<String, Class<?>> map = new HashMap<>();
        map.put("route.json", List.class);
        return map;
    }
}
