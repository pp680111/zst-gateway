package com.zst.discovery.registry.configuration;

import com.zst.discovery.registry.RegistryCenterClient;
import com.zst.discovery.registry.RegistryCenterService;
import com.zst.discovery.registry.RegistryClusterHelper;
import com.zst.discovery.dispatcher.loadbalancer.LoadBalancer;
import com.zst.discovery.dispatcher.loadbalancer.RandomLoadBalancer;
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

    @Bean
    public LoadBalancer<?> randomLoadBalancer() {
        return new RandomLoadBalancer();
    }
}
