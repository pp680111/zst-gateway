package com.zst.dispatcher;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ServiceDiscoveryDispatcher {
    public Mono<ServerResponse> doDispatch(ServerRequest request) {
        return ServerResponse.ok().bodyValue("hello");
    }
}
