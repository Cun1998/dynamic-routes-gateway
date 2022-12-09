package com.bestlink.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DynamicRoutesGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicRoutesGatewayApplication.class, args);
    }


}
