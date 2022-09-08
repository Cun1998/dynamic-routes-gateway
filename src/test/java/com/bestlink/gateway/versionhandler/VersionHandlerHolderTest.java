package com.bestlink.gateway.versionhandler;

import com.bestlink.gateway.entity.Route;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;

/**
 * @author ChuCun JH050169
 * @date 2022/9/7
 */
@SpringBootTest
class VersionHandlerHolderTest {

    @Autowired
    private VersionHandlerHolder versionHandlerHolder;

    @Test
    void handleRequest() {
        versionHandlerHolder.handleRequest(new ArrayList<ServiceInstance>(), new ArrayList<Route>(), new HttpHeaders());
    }
}
