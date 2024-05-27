package com.zst.gateway.discovery.registry;

import com.zst.gateway.discovery.registry.model.InstanceMetadata;
import com.zst.gateway.discovery.registry.model.Server;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class RegistryCenterService {
    private static final int SERVICE_RENEW_INTERVAL_MS = 5 * 1000;
    private ScheduledExecutorService scheduledExecutorService;
    private RegistryClusterHelper clusterHelper;
    private RegistryCenterClient client;

    public RegistryCenterService(RegistryClusterHelper clusterHelper, RegistryCenterClient client) {
        if (clusterHelper == null || client == null) {
            throw new IllegalArgumentException();
        }

        this.clusterHelper = clusterHelper;
        this.client = client;
    }

    public void start() {
//        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutorService.scheduleAtFixedRate(this::refreshServiceStatus, 5,
//                SERVICE_RENEW_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public void stop() {
//        try {
//            scheduledExecutorService.shutdown();
//            if (scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
//                throw new RuntimeException("shutdown scheduler failed");
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
    }

//    @Override
//    public void subscribe(RegistryChangedEventListener listener) {
//        // TODO 定时从注册中心上拉取当前已监听的Service的示例
//    }

    public Mono<List<InstanceMetadata>> fetchAll(String serviceId) {
        Server targetServer = clusterHelper.getLeader();
        if (targetServer == null) {
            throw new RuntimeException("Registry service not available, no leader found");
        }

        try {
            CompletableFuture<List<InstanceMetadata>> instances = client.getInstances(targetServer.getAddress(), serviceId);
            instances = instances.exceptionally(ex -> {
                log.error(ex.getMessage(), ex);
                return Collections.emptyList();
            });

            return Mono.fromFuture(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
