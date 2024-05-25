package com.zst.discovery.zstregistry;

import com.alibaba.fastjson2.JSONArray;
import com.zst.discovery.zstregistry.exception.ClientInvokeException;
import com.zst.discovery.zstregistry.model.InstanceMetadata;
import com.zst.discovery.zstregistry.model.Server;
import com.zst.utils.HttpInvoker;
import com.zst.utils.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class RegistryCenterClient {
    private int timeout = 5_000;

    private HttpInvoker httpInvoker;

    public RegistryCenterClient() {
//        this.timeout = props.getConnectTimeoutMs();
        this.httpInvoker = new HttpInvoker();
    }

    /**
     * 获取指定serviceId的实例列表
     * @param address
     * @param serviceId
     * @return
     */
    public CompletableFuture<List<InstanceMetadata>> getInstances(String address, String serviceId) {
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(serviceId)) {
            throw new IllegalArgumentException();
        }

        String url = String.format("%s/getInstances", address);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("serviceId", serviceId);

        try {
            CompletableFuture<HttpResponse> responseFuture = httpInvoker.doGet(url, null, urlParams);
            return responseFuture.thenApply(response -> {
                try {
                    String rawBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    return JSONArray.parseArray(rawBody, InstanceMetadata.class);
                } catch (Exception e) {
                    throw new ClientInvokeException("解析注册中心返回的数据时发生错误", e);
                }
            });
        } catch (Exception e) {
            throw new ClientInvokeException("调用注册中心接口时发生错误", e);
        }
    }

    /**
     * 获取指定serviceId的服务版本
     * @param address
     * @param serviceId
     * @return
     */
    public Long getVersion(String address, String serviceId) {
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(serviceId)) {
            throw new IllegalArgumentException();
        }

        String url = String.format("%s/version", address);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("serviceId", serviceId);

        try {
            Future<HttpResponse> responseFuture = httpInvoker.doGet(url, null, urlParams);
            HttpResponse response = responseFuture.get(timeout, TimeUnit.MILLISECONDS);

            String rawBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return Long.parseLong(rawBody);
        } catch (Exception e) {
            throw new ClientInvokeException("调用注册中心接口时发生错误", e);
        }
    }

    public void renew(String address, String serviceId, InstanceMetadata instanceMetadata) {
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(serviceId) || instanceMetadata == null) {
            throw new IllegalArgumentException();
        }

        String url = String.format("%s/renew", address);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("serviceId", serviceId);

        try {
            Future<HttpResponse> responseFuture = httpInvoker.doPost(url, null, urlParams, instanceMetadata);
            HttpResponse response = responseFuture.get(timeout, TimeUnit.MILLISECONDS);

            if (response.getStatusLine().getStatusCode() < 200
                    || response.getStatusLine().getStatusCode() >= 300) {
                throw new ClientInvokeException("续期失败");
            }
        } catch (Exception e) {
            throw new ClientInvokeException("调用注册中心接口时发生错误", e);
        }
    }

    /**
     * 注册服务实例
     * @param address
     * @param serviceId
     * @param instanceMetadata
     */
    public void register(String address, String serviceId, InstanceMetadata instanceMetadata) {
        if (StringUtils.isEmpty(address) || instanceMetadata == null) {
            throw new IllegalArgumentException();
        }

        String url = String.format("%s/register", address);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("serviceId", serviceId);

        try {
            Future<HttpResponse> responseFuture = httpInvoker.doPut(url, null, urlParams, instanceMetadata);
            HttpResponse response = responseFuture.get(timeout, TimeUnit.MILLISECONDS);

            if (response.getStatusLine().getStatusCode() < 200
                    || response.getStatusLine().getStatusCode() >= 300) {
                throw new ClientInvokeException("注册失败");
            }
        } catch (Exception e) {
            throw new ClientInvokeException("调用注册中心接口时发生错误", e);
        }
    }

    public void unregister(String address, String serviceId, InstanceMetadata instanceMetadata) {
        // TODO
    }


    // ****************************************************
    // 集群机制相关接口
    // ****************************************************

    /**
     * 获取集群服务列表
     * @param address
     * @return
     */
    public List<Server> clusterServerList(String address) {
        if (StringUtils.isEmpty(address)) {
            throw new IllegalArgumentException();
        }

        String url = String.format("%s/clusterServerList", address);

        try {
            Future<HttpResponse> responseFuture = httpInvoker.doGet(url, null, null);
            HttpResponse response = responseFuture.get(timeout, TimeUnit.MILLISECONDS);

            String rawBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return JSONArray.parseArray(rawBody, Server.class);
        } catch (Exception e) {
            throw new ClientInvokeException("调用注册中心接口时发生错误", e);
        }
    }
}
