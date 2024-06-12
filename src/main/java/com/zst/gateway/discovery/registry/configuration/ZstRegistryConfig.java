package com.zst.gateway.discovery.registry.configuration;

import com.zst.gateway.discovery.registry.RegistryCenterClient;
import com.zst.gateway.discovery.registry.RegistryCenterService;
import com.zst.gateway.discovery.registry.RegistryClusterHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(ZstRegistryProperties.class)
public class ZstRegistryConfig {
    @Bean
    public RegistryCenterClient registryCenterClient() {
        return new RegistryCenterClient();
    }

    @Bean
    public RegistryClusterHelper registryClusterHelper(ZstRegistryProperties zstRegistryProperties) {
        return new RegistryClusterHelper(registryCenterClient(), zstRegistryProperties.getAddress());
    }

    @Bean
    public RegistryCenterService registryCenterService(RegistryClusterHelper registryClusterHelper,
                                                       RegistryCenterClient registryCenterClient) {
        return new RegistryCenterService(registryClusterHelper, registryCenterClient);
    }
}
