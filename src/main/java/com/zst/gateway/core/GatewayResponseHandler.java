package com.zst.gateway.core;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
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

        // 注意这里的then，之前没研究过这个operator的效果，可能会出问题
        return Mono.just(new GatewayServerWebExchange(exchange))
                .map(this::doPreHandle)
                .flatMap(this::executeRequest)
                .map(this::doPostHandle)
                .then();
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

    private Mono<GatewayServerWebExchange> executeRequest(GatewayServerWebExchange exchange) {
        return WebClient.create(exchange.getRequestUrl())
                .method(exchange.getRequestMethod())
                .headers(httpHeaders -> httpHeaders.addAll(exchange.getRequestHeader()))
                .body(BodyInserters.fromDataBuffers(exchange.getRequestBody()))
                .exchangeToMono(clientResponse -> {
                    exchange.setGatewayProxyResponse(new GatewayProxyResponse(clientResponse));
                    return Mono.just(exchange);
                });

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
