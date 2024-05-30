package com.zst.gateway.configuration;

import com.zst.gateway.core.GatewayEntranceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public GatewayEntranceHandler gatewayEntranceRouter() {
        return new GatewayEntranceHandler();
    }
}
