package com.zst.gateway.discovery.dispatcher;

import com.zst.gateway.discovery.exception.NoAvailableServiceInstanceException;
import com.zst.gateway.discovery.registry.model.InstanceMetadata;
import com.zst.gateway.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * 在基于服务发现的请求转发流程中使用的ServerRequest包装类
 * @deprecated
 */
@Getter
@Setter
@Deprecated
public class ServiceDiscoveryServerRequest {
    private ServerRequest serverRequest;
    private String serviceId;
    private String path;
    private InstanceMetadata serviceInstance;

    public String buildTargetUrl() {
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
