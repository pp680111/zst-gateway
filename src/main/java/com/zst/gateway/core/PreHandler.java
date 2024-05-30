package com.zst.gateway.core;

/**
 * 网关调用链路上的前置处理器接口
 */
public interface PreHandler {
    GatewayServerWebExchange handle(GatewayServerWebExchange exchange);
}
