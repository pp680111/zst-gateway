package com.zst.gateway.discovery.dispatcher.loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer<T> implements LoadBalancer<T> {
    Random random = new Random();

    @Override
    public T choose(List<T> provider) {
        if (provider == null || provider.isEmpty()) {
            return null;
        }

        if (provider.size() == 1) {
            return provider.get(0);
        }

        return provider.get(random.nextInt(provider.size()));
    }
}
