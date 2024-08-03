package com.sunpe.mygrpc.base.discovery;

import com.sunpe.mygrpc.base.vo.ServiceInstance;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Resolver extends NameResolver {
    private final URI target;
    private final String serviceName;
    private final String group;
    private final Discovery discovery;

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
    public void start(Listener2 listener) {
        List<ServiceInstance> instances = this.discovery.getInstances(this.group, this.serviceName);
        List<EquivalentAddressGroup> addressGroups = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            SocketAddress socketAddress = new InetSocketAddress(instance.getAddress(), instance.getPort());
            Attributes attributes = Attributes.newBuilder().set(Attributes.Key.create("weight"), instance.getWeight()).build();
            EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(socketAddress, attributes);
            addressGroups.add(addressGroup);
        }
        ResolutionResult result = ResolutionResult.newBuilder().setAddresses(addressGroups).build();
        listener.onResult(result);
    }

    @Override
    public void shutdown() {
        if (this.discovery != null) {
            try {
                this.discovery.close();
            } catch (Exception e) {
                // todo
            }
        }
    }

}
