package com.zst.gateway.discovery.registry;

import com.zst.gateway.discovery.registry.model.Server;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 负责监控并刷新当前注册中心服务集群的状态，提供集群信息的类
 */
@Slf4j
public class RegistryClusterHelper {
    private RegistryCenterClient client;
    private List<String> initialServerAddresses;
    private List<Server> serverInstances;
    private Server leader;
    private ScheduledExecutorService executor;

    public RegistryClusterHelper(RegistryCenterClient client, List<String> initialServerAddresses) {
        if (client == null || initialServerAddresses == null || initialServerAddresses.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.client = client;
        this.initialServerAddresses = initialServerAddresses;
    }

    @PostConstruct
    public void init() {
        // 启动定时任务，定时刷新集群状态
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::refreshCluster, 0, 10, java.util.concurrent.TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        executor.shutdown();
        try {
            if (executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                throw new RuntimeException("shutdown executor failed");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Server getLeader() {
        if (leader == null) {
            refreshCluster();
        }
        return leader;
    }

    private void refreshCluster() {
        refreshClusterNodes();

        if (serverInstances == null || serverInstances.isEmpty()) {
            log.warn("no usable cluster node");
            return;
        }

        for (Server server : serverInstances) {
            if (server.isLeader() && server.isStatus()) {
                this.leader = server;
                break;
            }
        }
    }

    private void refreshClusterNodes() {
        // 如果有leader，则直接从leader获取集群信息
        if (leader != null) {
            try {
                List<Server> newestServerList = client.clusterServerList(leader.getAddress());
                this.serverInstances = newestServerList;
                return;
            } catch (Exception e) {
                log.error("refresh cluster from leader {} failed, fallback to other cluster node", leader.getAddress(), e);
            }
        }

        // 如果已经从注册中心集群获取到节点列表，则访问这部分节点
        if (serverInstances != null) {
            for (Server server : serverInstances) {
                try {
                    List<Server> newestServerList = client.clusterServerList(server.getAddress());
                    this.serverInstances = newestServerList;
                    return;
                } catch (Exception e) {
                    log.error("refresh cluster from node {} failed", server.getAddress(), e);
                }
            }
        }

        // 如果以上流程都获取失败的话，那么就回退到使用初始的节点地址列表
        for (String address : initialServerAddresses) {
            try {
                List<Server> newestServerList = client.clusterServerList(address);
                this.serverInstances = newestServerList;
                return;
            } catch (Exception e) {
                log.error("refresh cluster from address {} failed", address, e);
            }
        }
    }
}
