package com.zst.discovery.dispatcher;

import com.alibaba.fastjson2.JSON;
import com.zst.discovery.dispatcher.loadbalancer.LoadBalancer;
import com.zst.discovery.registry.RegistryCenterService;
import com.zst.discovery.exception.NoAvailableServiceInstanceException;
import com.zst.discovery.registry.model.InstanceMetadata;
import com.zst.discovery.registry.model.Server;
import com.zst.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ServiceDiscoveryDispatcher {
    @Autowired
    private RegistryCenterService registryCenterService;
    @Autowired
    private LoadBalancer<InstanceMetadata> loadBalancer;

    public Mono<ServerResponse> doDispatch(ServerRequest request) {
        Mono.just(request)
                .map(this::parseRequest)
                .flatMap(this::determineServiceInstance)

    }


    /**
     * 解析请求中的服务信息
     * @param request
     * @return
     */
    public ServiceDiscoveryServerRequest parseRequest(ServerRequest request) {
        String serviceId = request.pathVariables().get("serviceId");
        if (serviceId == null) {
            throw new IllegalArgumentException("serviceId not found");
        }

        String path = request.uri().getPath();
        path = path.substring(serviceId.length() + 1);

        ServiceDiscoveryServerRequest wrapRequest = new ServiceDiscoveryServerRequest();
        wrapRequest.setServerRequest(request);
        wrapRequest.setServiceId(serviceId);
        wrapRequest.setPath(path);

        return wrapRequest;
    }

    /**
     * 确定请求使用的服务实例
     * @param request
     */
    public Mono<ServiceDiscoveryServerRequest> determineServiceInstance(ServiceDiscoveryServerRequest request) {
        if (StringUtils.isEmpty(request.getServiceId())) {
            throw new IllegalArgumentException("serviceId not found");
        }

        return registryCenterService.fetchAll(request.getServiceId())
                .doOnNext(instanceList -> {
                    if (instanceList == null || instanceList.isEmpty()) {
                        throw new NoAvailableServiceInstanceException("No avaliable service instance found");
                    }
                })
                .map(loadBalancer::choose)
                .map(instance -> {
                    request.setServiceInstance(instance);
                    return request;
                });
    }

    private Mono<ServerResponse> doProxyInvoke(ServiceDiscoveryServerRequest request) {
        HttpHeaders sourceHeaders = request.getServerRequest().headers().asHttpHeaders();

        WebClient.create(request.buildTargetUrl())
                .method(request.getServerRequest().method())
                .headers(headers -> headers.addAll(sourceHeaders))
                .exchangeToMono(response -> {
                    HttpHeaders responseHeaders = response.headers().asHttpHeaders();
                    MediaType contentType = responseHeaders.getContentType();
                    ServerResponse serverResponse = ServerResponse.status(response.statusCode())
                            .headers(headers -> headers.addAll(responseHeaders))
                            .bodyValue(BodyInserters.fromPublisher(response.bodyToFlux(String.class), String.class));
                    if (contentType == MediaType.APPLICATION_JSON) {
                        response.body(BodyExtractors.toMono(String.class))
                                .map(jsonBody -> {
                                    return ServerResponse.status(response.statusCode())
                                            .headers(headers -> headers.addAll(responseHeaders))
                                            .body(BodyInserters.fromValue(jsonBody));
                                })
                    }

                });
    }

}
