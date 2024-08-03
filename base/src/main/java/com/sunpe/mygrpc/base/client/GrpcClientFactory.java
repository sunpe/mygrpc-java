package com.sunpe.mygrpc.base.client;

import com.sunpe.mygrpc.base.discovery.DiscoveryRegistry;
import com.sunpe.mygrpc.base.vo.GrpcClientConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GrpcClientFactory {

    private final String registry;
    private final String group;

    private final Map<String, GrpcClient> clients = new ConcurrentHashMap<>();

    public GrpcClientFactory(GrpcClientConfig config) throws Exception {
        if (config == null
                || config.getRegistry() == null || config.getRegistry().isEmpty()
                || config.getDiscoveryClass() == null
                || config.getGroup() == null || config.getGroup().isEmpty()) {
            throw new IllegalArgumentException("invalid grpc client config");
        }
        this.registry = config.getRegistry();
        this.group = config.getGroup();
        DiscoveryRegistry.register(this.registry, config.getDiscoveryClass());
    }

    public GrpcClient create(String serviceName) {
        String target = target(this.group, serviceName);
        return clients.computeIfAbsent(target, GrpcClient::new);
    }

    private String target(String group, String serviceName) {
        return registry + "/" + group + "/" + serviceName;
    }
}
