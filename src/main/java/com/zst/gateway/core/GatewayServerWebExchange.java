package com.zst.gateway.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

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
}
