package com.zst.discovery.dispatcher.loadbalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {
    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public T choose(List<T> provider) {
        if (provider == null || provider.isEmpty()) {
            return null;
        }

        if (provider.size() == 1) {
            return provider.get(0);
        }

        // 用位运算来保证index在溢出之后依旧返回正数
        return provider.get((index.getAndIncrement() & 0x7fffffff) % provider.size());
    }
}
