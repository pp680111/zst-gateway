package com.zst.gateway.discovery.dispatcher;

import com.zst.gateway.discovery.dispatcher.loadbalancer.LoadBalancer;
import com.zst.gateway.discovery.exception.NoAvailableServiceInstanceException;
import com.zst.gateway.discovery.registry.RegistryCenterService;
import com.zst.gateway.discovery.registry.model.InstanceMetadata;
import com.zst.gateway.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * @deprecated
 */
@Deprecated
@Component
public class ServiceDiscoveryDispatcher {
    @Autowired
    private RegistryCenterService registryCenterService;
    @Autowired
    private LoadBalancer<InstanceMetadata> loadBalancer;

    public Mono<ServerResponse> doDispatch(ServerRequest request) {
        return Mono.just(request)
                .map(this::parseRequest)
                .flatMap(this::determineServiceInstance)
                .flatMap(this::doProxyInvoke);
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

        if (StringUtils.isNotEmpty(request.uri().getQuery())) {
            path += "?" + request.uri().getQuery();
        }

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

        return WebClient.create(request.buildTargetUrl())
                .method(request.getServerRequest().method())
                .headers(headers -> headers.addAll(sourceHeaders))
                .body(BodyInserters.fromDataBuffers(request.getServerRequest().body(BodyExtractors.toDataBuffers())))
                .exchangeToMono(response -> {
//                    return response.bodyToMono(String.class)
//                            .map(body -> ServerResponse
//                                    .status(response.statusCode())
//                                    .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
//                                    .bodyValue(body));
                    return response.body(BodyExtractors.toDataBuffers()).collect(ArrayList<DataBuffer>::new, ArrayList::add)
                            .map(buffers -> ServerResponse
                                    .status(response.statusCode())
                                    .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                                    .body(BodyInserters.fromDataBuffers(Flux.fromIterable(buffers))));

                    /*
                       这种方式处理的话，看起来response的body部分还没有读渠道数据的时候，就已经返回了，
                       因此body部分读不到数据一直为空，需要像上面这部分的代码一样先从ClientResponse读取body数据之后
                       下一步的map转换成ServerResponse，才能保证响应体有的转发
                     */
//                    return ServerResponse
//                            .status(response.statusCode())
//                            .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
//                            .body(BodyInserters.fromDataBuffers(response.body(BodyExtractors.toDataBuffers())));
                })
                .flatMap(response -> response);
    }

}
