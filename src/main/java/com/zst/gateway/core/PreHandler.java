package com.zst.gateway.core;

import reactor.core.publisher.Mono;

/**
 * 网关调用链路上的前置处理器接口
 */
public interface PreHandler {
    Mono<GatewayServerWebExchange> handle(GatewayServerWebExchange exchange);
}
