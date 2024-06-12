package com.zst.gateway.configuration;

import com.zst.gateway.core.GatewayEntranceRegister;
import com.zst.gateway.core.GatewayHandlerMapping;
import com.zst.gateway.core.GatewayWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
//    @Bean
    public GatewayEntranceRegister gatewayEntranceRouter() {
        return new GatewayEntranceRegister();
    }

    @Bean
    public GatewayWebHandler gatewayWebHandler() {
        return new GatewayWebHandler();
    }

    @Bean
    public GatewayHandlerMapping gatewayHandlerMapping(GatewayWebHandler gatewayWebHandler) {
        return new GatewayHandlerMapping(gatewayWebHandler);
    }

//    @Deprecated
//    @Bean
//    public ServiceDiscoveryDispatcher serviceDiscoveryDispatcher() {
//        return new ServiceDiscoveryDispatcher();
//    }
//
//    @Deprecated
//    @Bean
//    public RouterFunction<ServerResponse> serviceDiscoveryRoute(ServiceDiscoveryDispatcher serviceDiscoveryDispatcher) {
//        return RouterFunctions.route()
//                .path("/{serviceId}/**", builder -> builder
//                        .GET(serviceDiscoveryDispatcher::doDispatch)
//                        .POST(serviceDiscoveryDispatcher::doDispatch)
//                ).build();
//    }
}
