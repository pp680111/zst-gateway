package com.zst.gateway.core;

/**
 *
 */
public interface PostHandler {
    GatewayServerWebExchange handle(GatewayServerWebExchange exchange);
}
