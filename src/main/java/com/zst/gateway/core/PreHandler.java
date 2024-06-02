package com.zst.gateway.core;

import reactor.core.publisher.Mono;

/**
 * 网关调用链路上的前置处理器接口
 *
 * 之所以Handler的入参和结果都是pipeline类型，是因为打算把每个Handler都当作一个往Pipeline上插入Operator的工具来用
 */
public interface PreHandler {
    Mono<GatewayServerWebExchange> handle(Mono<GatewayServerWebExchange> exchange);
}
