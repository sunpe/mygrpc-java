package com.sunpe.mygrpc.zookeeper;

import com.sunpe.mygrpc.base.discovery.Discovery;
import com.sunpe.mygrpc.base.vo.ServiceInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ZkServiceDiscovery implements Discovery {

    private final CuratorFramework client;
    private final ServiceDiscovery<Payload> discovery;
    private final Map<String, ServiceCache<Payload>> serviceCaches = new ConcurrentHashMap<>();
    private volatile boolean started;
    public final String basePath = "/grpc/service";

    public ZkServiceDiscovery(URI uri) {
        this.client = zkClient(uri.getAuthority());
        this.discovery = getServiceDiscovery();
    }

    @Override
    public List<ServiceInstance> getInstances(String group, String serviceName) {
        if (!started) {
            throw new IllegalStateException("ZkServiceDiscovery is not started");
        }
        String groupServiceName = getGroupServiceName(group, serviceName);
        Collection<org.apache.curator.x.discovery.ServiceInstance<Payload>> instances = null;
        try {
            instances = getInstances(groupServiceName);
        } catch (Exception e) {
            throw new RuntimeException("get instances failed", e);
        }
        List<ServiceInstance> serviceInstances = new ArrayList<>(instances.size());

        for (org.apache.curator.x.discovery.ServiceInstance<Payload> instance : instances) {
            serviceInstances.add(new ServiceInstance(instance.getId(), "", "", instance.getAddress(),
                    instance.getPort(), instance.getPayload().getWeight()));
        }
        return serviceInstances;
    }

    private Collection<org.apache.curator.x.discovery.ServiceInstance<Payload>> getInstances(String serviceName) throws Exception {
        ServiceCache<Payload> cache = serviceCaches.computeIfAbsent(serviceName, s -> {
            ServiceCache<Payload> serviceCache = discovery.serviceCacheBuilder()
                    .name(serviceName).build();
            try {
                serviceCache.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return serviceCache;
        });
        List<org.apache.curator.x.discovery.ServiceInstance<Payload>> instances = cache.getInstances();
        if (instances != null && !instances.isEmpty()) {
            return instances;
        }
        return discovery.queryForInstances(serviceName);
    }

    @Override
    public boolean registerInstance(ServiceInstance instance) throws Exception {
        if (!started) {
            throw new IllegalStateException("ZkServiceDiscovery is not started");
        }
        org.apache.curator.x.discovery.ServiceInstance<Payload> serviceInstance = getServiceInstanceBuilder(instance);
        discovery.registerService(serviceInstance);
        return true;
    }

    @Override
    public boolean unregisterInstance(ServiceInstance instance) throws Exception {
        if (!started) {
            throw new IllegalStateException("ZkServiceDiscovery is not started");
        }
        org.apache.curator.x.discovery.ServiceInstance<Payload> serviceInstance = getServiceInstanceBuilder(instance);
        discovery.unregisterService(serviceInstance);
        return true;
    }

    @Override
    public synchronized void start() throws Exception {
        if (started) {
            return;
        }
        client.start();
        client.blockUntilConnected(1, TimeUnit.MINUTES);
        discovery.start();

        started = true;
    }

    @Override
    public synchronized void close() throws Exception {
        if (!started) {
            throw new IllegalStateException("ZkServiceDiscovery is not started");
        }
        for (ServiceCache<Payload> serviceCache : serviceCaches.values()) {
            serviceCache.close();
        }
        if (discovery != null) {
            discovery.close();
        }
        if (client != null) {
            client.close();
        }
    }

    private CuratorFramework zkClient(String zkServers) {
        return CuratorFrameworkFactory.builder()
                .connectString(zkServers)
                .retryPolicy(new RetryNTimes(3, 1000))
                .build();
    }

    private ServiceDiscovery<Payload> getServiceDiscovery() {
        return ServiceDiscoveryBuilder
                .builder(Payload.class)
                .serializer(new JsonInstanceSerializer<>(Payload.class))
                .client(this.client)
                .basePath(basePath)
                .build();
    }

    private org.apache.curator.x.discovery.ServiceInstance<Payload> getServiceInstanceBuilder(ServiceInstance instance) throws Exception {
        ServiceInstanceBuilder<Payload> builder = org.apache.curator.x.discovery.ServiceInstance.builder();
        builder.id(instance.getServiceID())
                .name(getGroupServiceName(instance.getGroup(), instance.getServiceName()))
                .address(instance.getAddress())
                .port(instance.getPort());
        if (instance.getWeight() > 0) {
            builder.payload(new Payload(instance.getWeight()));
        }
        return builder.build();
    }

    private String getGroupServiceName(String group, String serviceName) {
        return group + "#" + serviceName;
    }
}