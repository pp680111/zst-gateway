package com.zst.gateway.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.List;

@Getter
@Setter
public class GatewayProxyResponse {
    public GatewayProxyResponse(ClientResponse clientResponse) {
        this.clientResponse = clientResponse;
    }

    private ClientResponse clientResponse;
    private List<DataBuffer> bodyDataBuffers;
}
