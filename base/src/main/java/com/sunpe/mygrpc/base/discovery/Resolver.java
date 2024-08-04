package com.sunpe.mygrpc.base.discovery;

import com.sunpe.mygrpc.base.vo.ServiceInstance;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Resolver extends NameResolver {
    private final URI target;
    private final String serviceName;
    private final String group;
    private final Discovery discovery;
    private volatile boolean started;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    Resolver(URI uri, Args args) throws Exception {
        // target uri scheme://${ip}:${port},${ip}:${port}.../${group}/${service_name}/
        this.target = uri;
        this.discovery = DiscoveryRegistry.getDiscovery(uri);
        this.discovery.start();
        String path = target.getPath();
        String[] ps = path.split("/");
        this.group = ps[1];
        this.serviceName = ps[2];
    }

    @Override
    public String getServiceAuthority() {
        return this.target.getAuthority();
    }

    @Override
    public synchronized void start(Listener2 listener) {
        ResolutionResult result = getInstances();
        listener.onResult(result);
        started = true;
        // observe instances update
        executor.scheduleWithFixedDelay(() -> observeDiscovery(listener), 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public synchronized void shutdown() {
        if (this.discovery != null) {
            try {
                this.discovery.close();
            } catch (Exception e) {
                // todo
            }
        }
        executor.shutdown();
        started = false;
    }

    private ResolutionResult getInstances() {
        List<ServiceInstance> instances = this.discovery.getInstances(this.group, this.serviceName);
        List<EquivalentAddressGroup> addressGroups = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            SocketAddress socketAddress = new InetSocketAddress(instance.getAddress(), instance.getPort());
            Attributes attributes = Attributes.newBuilder().set(Attributes.Key.create("weight"), instance.getWeight()).build();
            EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(socketAddress, attributes);
            addressGroups.add(addressGroup);
        }
        return ResolutionResult.newBuilder().setAddresses(addressGroups).build();
    }

    private void observeDiscovery(Listener2 listener) {
        if (!started) {
            return;
        }
        //
        if (this.discovery.instancesUpdate()) {
            logger.warn("service [{}] group[{}] instances update message receive, update instance in resolver now",
                    serviceName, group);
            ResolutionResult result = getInstances();
            listener.onResult(result);
        }
    }
}
