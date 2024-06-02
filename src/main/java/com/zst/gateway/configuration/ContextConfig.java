package com.zst.gateway.configuration;

import com.zst.gateway.core.PreHandler;
import com.zst.gateway.discovery.dispatcher.ServiceDiscoveryDispatcher;
import com.zst.gateway.discovery.dispatcher.ServiceDiscoveryPreHandler;
import com.zst.gateway.discovery.dispatcher.loadbalancer.LoadBalancer;
import com.zst.gateway.discovery.dispatcher.loadbalancer.RoundRobinLoadBalancer;
import com.zst.gateway.discovery.registry.RegistryCenterService;
import com.zst.gateway.discovery.registry.configuration.ZstRegistryConfig;
import com.zst.gateway.discovery.registry.model.InstanceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Import(ZstRegistryConfig.class)
@Configuration
public class ContextConfig {
    @Bean
    public LoadBalancer<?> loadBalancer() {
        return new RoundRobinLoadBalancer<>();
    }

    @Bean
    public PreHandler ServiceDiscoveryPreHandler(RegistryCenterService registryCenterService,
                                                 LoadBalancer<InstanceMetadata> loadBalancer) {
        return new ServiceDiscoveryPreHandler(registryCenterService, loadBalancer);
    }
}
