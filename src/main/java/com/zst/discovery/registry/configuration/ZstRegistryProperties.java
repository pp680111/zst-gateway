package com.zst.discovery.registry.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "zst.registry")
public class ZstRegistryProperties {
    List<String> address;
}
