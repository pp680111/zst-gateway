package com.zst.gateway.configuration;

import com.zst.gateway.core.GatewayEntranceRegister;
import com.zst.gateway.core.GatewayWebHandler;
import com.zst.gateway.discovery.dispatcher.ServiceDiscoveryDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WebConfig {
    @Bean
    public GatewayEntranceRegister gatewayEntranceRouter() {
        return new GatewayEntranceRegister();
    }

    @Bean
    public GatewayWebHandler gatewayWebHandler() {
        return new GatewayWebHandler();
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
