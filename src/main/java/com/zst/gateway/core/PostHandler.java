package com.zst.gateway.core;

import reactor.core.publisher.Mono;

/**
 *
 */
public interface PostHandler {
    Mono<GatewayServerWebExchange> handle(Mono<GatewayServerWebExchange> exchange);
}
