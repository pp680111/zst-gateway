package com.zst.gateway.discovery.dispatcher.loadbalancer;

import java.util.List;

public interface LoadBalancer<T> {
    T choose(List<T> provider);

    LoadBalancer Default = p -> p == null || p.isEmpty() ? null : p.get(0);
}
