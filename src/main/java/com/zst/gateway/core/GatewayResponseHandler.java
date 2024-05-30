package com.zst.gateway.core;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public class GatewayResponseHandler {
    List<PreHandler> preHandlers;
    List<PostHandler> postHandlers;

    public Mono<Void> handle(ServerWebExchange exchange) {
        if (exchange == null) {
            return Mono.error(new IllegalArgumentException("exchange参数不得为空"));
        }

        Mono.just(new GatewayServerWebExchange(exchange))
                .map(this::doPreHandle)
                .doOnNext(this::executeRequest)
                .map(this::doPostHandle);
    }

    private GatewayServerWebExchange doPreHandle(GatewayServerWebExchange exchange) {
        if (preHandlers == null) {
            return exchange;
        }

        Optional<GatewayServerWebExchange> result = preHandlers.stream()
                .map(preHandler -> preHandler.handle(exchange))
                .findAny();
        if (result.isEmpty()) {
            throw new RuntimeException("preHandler处理失败");
        }

        return result.get();
    }

    private void executeRequest(GatewayServerWebExchange exchange) {

    }

    private GatewayServerWebExchange doPostHandle(GatewayServerWebExchange exchange) {
        if (postHandlers == null) {
            return exchange;
        }

        Optional<GatewayServerWebExchange> result = postHandlers.stream()
                .map(postHandler -> postHandler.handle(exchange))
                .findAny();
        if (result.isEmpty()) {
            throw new RuntimeException("postHandler处理失败");
        }

        return result.get();
    }

    private Mono<Void> finish(GatewayServerWebExchange exchange) {
        return Mono.empty();
    }
}
