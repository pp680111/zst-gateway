package com.zst.gateway.router;

import com.zst.gateway.discovery.dispatcher.ServiceDiscoveryDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

public class GatewayEntranceRouter {
    @Autowired
    private ServiceDiscoveryDispatcher serviceDiscoveryDispatcher;

    @Bean
    public RouterFunction<ServerResponse> serviceDiscoveryRoute() {
        return RouterFunctions.route()
                .path("/{serviceId}/**", builder -> builder
                        .GET(serviceDiscoveryDispatcher::doDispatch)
                        .POST(serviceDiscoveryDispatcher::doDispatch)
                ).build();
    }
}
