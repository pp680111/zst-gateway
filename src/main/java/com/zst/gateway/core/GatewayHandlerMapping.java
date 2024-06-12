package com.zst.gateway.core;

import org.springframework.web.reactive.handler.AbstractHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class GatewayHandlerMapping extends AbstractHandlerMapping {
    private GatewayWebHandler gatewayWebHandler;

    public GatewayHandlerMapping(GatewayWebHandler gatewayWebHandler) {
        this.gatewayWebHandler = gatewayWebHandler;
        setOrder(-1);
    }

    @Override
    protected Mono<?> getHandlerInternal(ServerWebExchange exchange) {
        return Mono.just(gatewayWebHandler);
    }
}
