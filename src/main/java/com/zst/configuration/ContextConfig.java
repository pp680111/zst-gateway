package com.zst.configuration;

import com.zst.discovery.registry.configuration.ZstRegistryConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(ZstRegistryConfig.class)
@Configuration
public class ContextConfig {
}
