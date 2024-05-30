package com.zst.gateway.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

/**
 * 网关ServerWebExchange对象
 */
@Getter
@Setter
@AllArgsConstructor
public class GatewayServerWebExchange {
    private ServerWebExchange serverWebExchange;

    /**
     * 构建完整的请求URL
     * @return
     */
    public String getRequestUrl() {
        throw new RuntimeException("未找到目标服务实例");
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
