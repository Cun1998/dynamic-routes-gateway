package com.bestlink.gateway.config;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("unchecked")
public class NacosConfigLocalCatch implements InitializingBean {
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2, 4, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String clazzSimpleName = getClass().getSimpleName();
    private final Map<String, Object> localCatchMap = new HashMap<>();
    @Resource(name = "nacosConfigLocalCacheInfoMap")
    private Map<String, Class<?>> nacosConfigLocalCacheInfoMap;
    @Resource
    private NacosConfigProperties nacosConfigProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        nacosConfigLocalCacheInfoMap.forEach((k, v) -> {
            NacosConfigInfo nacosConfigInfo = new NacosConfigInfo(nacosConfigProperties.getServerAddr(),
                    nacosConfigProperties.getNamespace(), nacosConfigProperties.getGroup(),
                    k, true, nacosConfigLocalCacheInfoMap.get(k));

            this.listener(nacosConfigInfo);
        });
    }

    public void listener(NacosConfigInfo nacosConfigInfo) {
        Listener listener = new Listener() {
            @Override
            public Executor getExecutor() {
                return threadPoolExecutor;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                logger.info("{}#receiveConfigInfo receive configInfo. configInfo={}", clazzSimpleName, configInfo);
                compile(configInfo, nacosConfigInfo);
            }
        };
        ConfigService configService = this.getConfigService(nacosConfigInfo);
        String config = null;
        try {
            config = configService.getConfig(nacosConfigInfo.getDataId(), nacosConfigInfo.getGroup(), nacosConfigInfo.getTimeout());
            logger.info("{}#afterPropertiesSet init configInfo. configInfo={}", clazzSimpleName, config);
            // 初始化
            compile(config, nacosConfigInfo);
            // 监听
            configService.addListener(nacosConfigInfo.getDataId(), nacosConfigInfo.getGroup(), listener);
        } catch (NacosException e) {
            e.printStackTrace();
            throw new RuntimeException("nacos server 监听 异常! dataId = " + nacosConfigInfo.getDataId());
        }
    }

    private void compile(String config, NacosConfigInfo nacosConfigInfo) {
        Object initValue = JSONObject.parseObject(config, nacosConfigInfo.getCls());
        localCatchMap.put(nacosConfigInfo.getDataId(), initValue);
    }

    /**
     * 获取ConfigService
     *
     * @return
     */
    private ConfigService getConfigService(NacosConfigInfo nacosConfigInfo) {
        String serverAddr = nacosConfigInfo.getServerAddr();
        String nameSpace = nacosConfigInfo.getNamespace();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, nameSpace);
        ConfigService configService;
        try {
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
            throw new RuntimeException("Nacos config 配置 异常");
        }
        return configService;
    }

    public <T> T get(String dataId, Class<T> cls) {
        if (cls != nacosConfigLocalCacheInfoMap.get(dataId)) {
            throw new IllegalArgumentException("类型异常");
        }

        return (T) localCatchMap.get(dataId);
    }

}
