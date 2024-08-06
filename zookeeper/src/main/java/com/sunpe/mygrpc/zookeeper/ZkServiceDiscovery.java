package com.sunpe.mygrpc.zookeeper;

import com.sunpe.mygrpc.base.discovery.Discovery;
import com.sunpe.mygrpc.base.vo.ServiceInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ZkServiceDiscovery implements Discovery {

    private final CuratorFramework client;
    private final ServiceDiscovery<Payload> discovery;
    private final Map<String, ServiceCache<Payload>> serviceCaches = new ConcurrentHashMap<>();
    private volatile boolean started;
    public final String basePath = "/mygrpc/service";
    private final ReentrantLock instanceChangeLock = new ReentrantLock();
    private final Condition instanceChangeCondition = instanceChangeLock.newCondition();

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
        Collection<org.apache.curator.x.discovery.ServiceInstance<Payload>> instances;
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
        ServiceCache<Payload> cache = serviceCaches.computeIfAbsent(serviceName, this::createServiceCache);

        List<org.apache.curator.x.discovery.ServiceInstance<Payload>> instances = cache.getInstances();
        if (instances != null && !instances.isEmpty()) {
            return instances;
        }
        return discovery.queryForInstances(serviceName);
    }

    @Override
    public void registerInstance(ServiceInstance instance) throws Exception {
        if (!started) {
            throw new IllegalStateException("ZkServiceDiscovery is not started");
        }
        org.apache.curator.x.discovery.ServiceInstance<Payload> serviceInstance = getServiceInstanceBuilder(instance);
        discovery.registerService(serviceInstance);
    }

    @Override
    public void unregisterInstance(ServiceInstance instance) throws Exception {
        if (!started) {
            throw new IllegalStateException("ZkServiceDiscovery is not started");
        }
        org.apache.curator.x.discovery.ServiceInstance<Payload> serviceInstance = getServiceInstanceBuilder(instance);
        discovery.unregisterService(serviceInstance);
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

    @Override
    public boolean instancesUpdate() {
        instanceChangeLock.lock();
        try {
            instanceChangeCondition.await();
        } catch (InterruptedException e) {
            // todo
        } finally {
            instanceChangeLock.unlock();
        }
        return true;
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

    private ServiceCache<Payload> createServiceCache(String serviceName) {
        ServiceCache<Payload> cache = discovery.serviceCacheBuilder().name(serviceName).build();
        try {
            cache.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        cache.addListener(new ServiceCacheListener() {
            @Override
            public void cacheChanged() {
                instanceChangeLock.lock();
                try {
                    instanceChangeCondition.signalAll();
                } finally {
                    instanceChangeLock.unlock();
                }
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
            }
        });
        return cache;
    }

    private String getGroupServiceName(String group, String serviceName) {
        return group + "#" + serviceName;
    }
}
