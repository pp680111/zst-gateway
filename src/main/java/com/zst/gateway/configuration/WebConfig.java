package com.zst.gateway.configuration;

import com.zst.gateway.core.GatewayEntranceRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public GatewayEntranceRegister gatewayEntranceRouter() {
        return new GatewayEntranceRegister();
    }
}
