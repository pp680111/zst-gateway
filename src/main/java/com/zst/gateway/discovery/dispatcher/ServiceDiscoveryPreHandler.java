package com.zst.gateway.discovery.dispatcher;

import com.zst.gateway.core.GatewayServerWebExchange;
import com.zst.gateway.core.PreHandler;
import com.zst.gateway.discovery.dispatcher.loadbalancer.LoadBalancer;
import com.zst.gateway.discovery.exception.NoAvailableServiceInstanceException;
import com.zst.gateway.discovery.registry.RegistryCenterService;
import com.zst.gateway.discovery.registry.model.InstanceMetadata;
import com.zst.gateway.utils.StringUtils;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ServiceDiscoveryPreHandler implements PreHandler {
    private RegistryCenterService registryCenterService;
    private LoadBalancer<InstanceMetadata> loadBalancer;

    @Override
    public Mono<GatewayServerWebExchange> handle(Mono<GatewayServerWebExchange> exchange) {
        return exchange
                .map(this::parseRequest)
                .flatMap(this::determineServiceInstance);
    }

    private GatewayServerWebExchange parseRequest(GatewayServerWebExchange exchange) {
        String serviceId = determineServiceIdFromRequestPath(exchange.getPath());
        String realRequestPath = determineRealRequestPath(exchange.getPath());

        ServiceDiscoveryServerWebExchange result = ServiceDiscoveryServerWebExchange.create(exchange);
        result.setServiceId(serviceId);
        result.setPath(realRequestPath);
        return result;
    }

    private Mono<GatewayServerWebExchange> determineServiceInstance(GatewayServerWebExchange exchange) {
        ServiceDiscoveryServerWebExchange sdExchange = (ServiceDiscoveryServerWebExchange) exchange;
        if (StringUtils.isEmpty(sdExchange.getServiceId())) {
            throw new IllegalArgumentException("cannot found serviceId");
        }

        return registryCenterService.fetchAll(sdExchange.getServiceId())
                .doOnNext(instanceList -> {
                    if (instanceList == null || instanceList.isEmpty()) {
                        throw new NoAvailableServiceInstanceException("No avaliable service instance found");
                    }
                })
                .map(loadBalancer::choose)
                .map(instance -> {
                    sdExchange.setServiceInstance(instance);
                    return sdExchange;
                });
    }

    private String determineServiceIdFromRequestPath(String requestPath) {
        // 更好的做法应该是根据配置的serviceId提取模板来匹配，这里就先这样吧，后面再优化
        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }

        int index = requestPath.indexOf("/");
        if (index == -1) {
            return "";
        }

        return requestPath.substring(0, index);
    }

    private String determineRealRequestPath(String requestPath) {
        // 同上
        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }

        int index = requestPath.indexOf("/");
        if (index == -1) {
            return "";
        }

        return requestPath.substring(index + 1);

    }
}
