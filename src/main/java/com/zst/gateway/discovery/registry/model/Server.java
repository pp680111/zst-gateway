package com.zst.gateway.discovery.registry.model;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Server {
    private String ip;
    private int port;
    private String address;
    private boolean status = false;
    private boolean isLeader = false;

    public String getIp() {
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }

        if (address != null) {
            return address.split(":")[0];
        }

        throw new IllegalArgumentException("server ip cannot be null");
    }

    public int getPort() {
        if (port > 0) {
            return port;
        }

        if (StringUtils.isNotEmpty(address)) {
            return Integer.parseInt(address.split(":")[1]);
        }

        throw new IllegalArgumentException("server port cannot be null");
    }

    public String getAddress() {
        // TODO 需要完善获取服务器地址的逻辑
        if (StringUtils.isNotEmpty(address)) {
            return "http://" + address;
        }

        if (StringUtils.isNotEmpty(ip) && port > 0) {
            return "http://" + ip + ":" + port;
        }

        return null;
    }
}
