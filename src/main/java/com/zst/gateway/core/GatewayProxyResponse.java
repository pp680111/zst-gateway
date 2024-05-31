package com.zst.gateway.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.function.client.ClientResponse;

@Getter
@Setter
public class GatewayProxyResponse {
    public GatewayProxyResponse(ClientResponse clientResponse) {
        this.clientResponse = clientResponse;
    }

    private ClientResponse clientResponse;
}
