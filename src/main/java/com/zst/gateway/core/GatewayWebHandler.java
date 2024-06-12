package com.zst.gateway.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网关的请求处理入口
 */
@Slf4j
public class GatewayWebHandler implements WebHandler, ApplicationContextAware, InitializingBean {
    ApplicationContext applicationContext;
    List<PreHandler> preHandlers;
    List<PostHandler> postHandlers;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        if (exchange == null) {
            return Mono.error(new IllegalArgumentException("exchange参数不得为空"));
        }

        return Mono.just(new GatewayServerWebExchange(exchange))
                .transform(this::doPreHandle)
                .flatMap(this::executeRequest)
                .transform(this::doPostHandle)
                .flatMap(GatewayServerWebExchange::writeResponse)
                .doOnError(e -> {
                    log.error("处理请求时发生错误", e);
                });
    }

    private void init() {
        Map<String, PreHandler> preHandlerBeans = applicationContext.getBeansOfType(PreHandler.class);
        this.preHandlers = new ArrayList<>(preHandlerBeans.values());

        Map<String, PostHandler> postHandlerBeans = applicationContext.getBeansOfType(PostHandler.class);
        this.postHandlers = new ArrayList<>(postHandlerBeans.values());
    }

    private Mono<GatewayServerWebExchange> doPreHandle(Mono<GatewayServerWebExchange> exchange) {
        if (preHandlers == null) {
            return exchange;
        }

        Mono<GatewayServerWebExchange> transformedExchange = exchange;
        for (PreHandler preHandler : preHandlers) {
            transformedExchange = transformedExchange.transform(preHandler::handle);
        }

        return transformedExchange;
    }

    private Mono<GatewayServerWebExchange> executeRequest(GatewayServerWebExchange exchange) {
        return WebClient.create(exchange.getRequestUrl())
                .method(exchange.getRequestMethod())
                .headers(httpHeaders -> httpHeaders.addAll(exchange.getRequestHeader()))
                .body(BodyInserters.fromDataBuffers(exchange.getRequestBody()))
                .exchangeToFlux(clientResponse -> {
                    exchange.setGatewayProxyResponse(new GatewayProxyResponse(clientResponse));
                    return clientResponse.body(BodyExtractors.toFlux(DataBuffer.class));
                })
                .collectList() // TODO 这里用collect将dataBuffer存起来的话，在遇到文件下载的时候会不会吃掉很多内存的？
                .map(dataBuffers -> {
                    exchange.getGatewayProxyResponse().setBodyDataBuffers(dataBuffers);
                    return exchange;
                });

    }

    private Mono<GatewayServerWebExchange> doPostHandle(Mono<GatewayServerWebExchange> exchange) {
        if (postHandlers == null) {
            return exchange;
        }

        Mono<GatewayServerWebExchange> transformedExchange = exchange;
        for (PostHandler postHandler : postHandlers) {
            transformedExchange = transformedExchange.transform(postHandler::handle);
        }

        return transformedExchange;
    }
}
