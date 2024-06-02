package com.zst.gateway.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Collections;
import java.util.Map;

/**
 * 网关ServerWebExchange对象
 */
@Getter
@Setter
public class GatewayServerWebExchange {
    private ServerWebExchange serverWebExchange;
    private GatewayProxyResponse gatewayProxyResponse;

    public GatewayServerWebExchange(ServerWebExchange serverWebExchange) {
        this.serverWebExchange = serverWebExchange;
    }

    /**
     * 构建完整的请求URL
     * @return
     */
    public String getRequestUrl() {
        throw new RuntimeException("未找到目标服务实例");
    }

    public String getPath() {
        return serverWebExchange.getRequest().getPath().value();
    }

    /**
     * 获取路径上的参数
     * @param parameterName
     * @return
     */
    public String getPathVariable(String parameterName) {
        Map<String, String> uriTemplateVariables = serverWebExchange.getAttributeOrDefault(
                RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.emptyMap());
        return uriTemplateVariables.get(parameterName);
    }

    public HttpMethod getRequestMethod() {
        return serverWebExchange.getRequest().getMethod();
    }

    /**
     * 获取请求体的头信息
     * @return
     */
    public HttpHeaders getRequestHeader() {
        return serverWebExchange.getRequest().getHeaders();
    }

    /**
     * 获取请求体数据
     *
     * @return
     */
    public Flux<DataBuffer> getRequestBody() {
        return serverWebExchange.getRequest().getBody();
    }

    public Mono<Void> writeResponse() {
        if (gatewayProxyResponse == null || gatewayProxyResponse.getBodyDataBuffers() == null
                || gatewayProxyResponse.getClientResponse() == null) {
            throw new IllegalArgumentException("响应数据为空");
        }

        ServerHttpResponse response = serverWebExchange.getResponse();
        ClientResponse proxyResponse = gatewayProxyResponse.getClientResponse();
        response.setStatusCode(proxyResponse.statusCode());
        response.getHeaders().addAll(proxyResponse.headers().asHttpHeaders());
        response.getCookies().addAll(proxyResponse.cookies());

        return response.writeWith(Flux.fromIterable(gatewayProxyResponse.getBodyDataBuffers()));
    }
}
