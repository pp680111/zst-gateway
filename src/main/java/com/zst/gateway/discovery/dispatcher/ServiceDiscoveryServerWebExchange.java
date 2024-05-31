package com.zst.gateway.discovery.dispatcher;

import com.zst.gateway.core.GatewayServerWebExchange;
import com.zst.gateway.discovery.exception.NoAvailableServiceInstanceException;
import com.zst.gateway.discovery.registry.model.InstanceMetadata;
import com.zst.gateway.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;

/**
 * 基于服务发现的网关请求
 */
@Getter
@Setter
public class ServiceDiscoveryServerWebExchange extends GatewayServerWebExchange  {
    private String serviceId;
    private String path;
    private InstanceMetadata serviceInstance;

    public ServiceDiscoveryServerWebExchange(ServerWebExchange serverWebExchange) {
        super(serverWebExchange);
    }

    @Override
    public String getRequestUrl() {
        if (serviceInstance == null) {
            throw new NoAvailableServiceInstanceException("No available service instance");
        }

        if (StringUtils.isEmpty(path)) {
            path = "";
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return String.format("http://%s:%d/%s", serviceInstance.getHost(), serviceInstance.getPort(), path);
    }
}
