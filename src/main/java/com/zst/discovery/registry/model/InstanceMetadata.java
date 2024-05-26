package com.zst.discovery.registry.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstanceMetadata {
    private String host;
    private int port;
    private String context = "/";
    private boolean status;
}
