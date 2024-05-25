package com.zst.dispatcher;

import com.zst.discovery.zstregistry.RegistryCenterService;
import com.zst.discovery.zstregistry.model.InstanceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ServiceDiscoveryDispatcher {
    @Autowired
    private RegistryCenterService registryCenterService;

    public Mono<ServerResponse> doDispatch(ServerRequest request) {
        return ServerResponse.ok().bodyValue("hello");
    }

//    private Mono<InstanceMetadata> getInstance(String serviceId) {
//        registryCenterService.
//    }
}
