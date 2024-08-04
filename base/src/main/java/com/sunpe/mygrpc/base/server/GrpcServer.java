package com.sunpe.mygrpc.base.server;

import com.sunpe.mygrpc.base.discovery.Discovery;
import com.sunpe.mygrpc.base.discovery.DiscoveryRegistry;
import com.sunpe.mygrpc.base.health.HealthCheckRequest;
import com.sunpe.mygrpc.base.health.HealthCheckResponse;
import com.sunpe.mygrpc.base.health.HealthGrpc;
import com.sunpe.mygrpc.base.utils.IpUtil;
import com.sunpe.mygrpc.base.vo.GrpcServerConfig;
import com.sunpe.mygrpc.base.vo.ServiceInstance;
import io.grpc.BindableService;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GrpcServer {

    private final String ip;
    private final int port;
    private final String group;
    private final ServerBuilder<?> serverBuilder;
    private final Discovery discovery;
    private volatile boolean started;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public GrpcServer(GrpcServerConfig config) throws Exception {
        if (config == null
                || config.getRegistry() == null || config.getRegistry().isEmpty()
                || config.getGroup() == null || config.getGroup().isEmpty()
                || config.getPort() == 0) {
            throw new IllegalArgumentException("invalid grpc server config");
        }
        registerDiscovery(config);

        this.group = config.getGroup();
        this.ip = IpUtil.getLocalIpV4Address();
        this.port = config.getPort();
        this.discovery = DiscoveryRegistry.getDiscovery(config.getRegistry());
        this.serverBuilder = ServerBuilder.forPort(port)
                .compressorRegistry(CompressorRegistry.getDefaultInstance())
                .decompressorRegistry(DecompressorRegistry.getDefaultInstance());
        this.serverBuilder.addService(new HealthChecker());
    }

    public void bindService(BindableService service) {
        if (started) {
            return;
        }
        logger.info("bind serve [{}] to grpc server", service.bindService().getServiceDescriptor().getName());
        serverBuilder.addService(service);
    }

    public void bindServices(Collection<BindableService> services) {
        if (started) {
            return;
        }
        for (BindableService service : services) {
            bindService(service);
        }
    }

    public synchronized void start() throws Exception {
        this.started = true;
        this.discovery.start();
        Server server = this.serverBuilder.build();
        server.start();
        this.register(server);
        logger.warn("grpc server start at [:{}]", server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("grpc server shutting down");
            logger.warn("grpc server shutting down");
            try {
                this.deregister(server);
                stop(server);
            } catch (Exception e) {
                logger.error("grpc server shut down error", e);
            }
            System.err.println("server has shut down");
        }));
        server.awaitTermination();
    }

    public boolean isRunning() {
        return started;
    }

    private void registerDiscovery(GrpcServerConfig config) throws Exception {
        DiscoveryRegistry.register(config.getRegistry(), config.getServiceDiscoveryClass());
    }

    private void register(Server server) throws Exception {
        List<ServiceInstance> instances = serviceInstances(server);
        for (ServiceInstance instance : instances) {
            this.discovery.registerInstance(instance);
        }
    }

    private void deregister(Server server) throws Exception {
        List<ServiceInstance> instances = serviceInstances(server);
        for (ServiceInstance instance : instances) {
            this.discovery.unregisterInstance(instance);
        }
    }

    private void stop(Server server) throws Exception {
        server.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        if (this.discovery != null) {
            this.discovery.close();
        }
    }

    private List<ServiceInstance> serviceInstances(Server server) {
        List<ServerServiceDefinition> services = server.getServices();
        List<ServiceInstance> instances = new ArrayList<>(services.size());
        for (ServerServiceDefinition service : services) {
            String serviceName = service.getServiceDescriptor().getName();
            if (serviceName.equals(HealthGrpc.SERVICE_NAME)) {
                continue;
            }
            String serviceId = serviceId(serviceName);
            instances.add(new ServiceInstance(serviceId, serviceName, this.group, this.ip, this.port, 10));
        }
        return instances;
    }

    private String serviceId(String service) {
        return this.group + ":" + service + ":" + this.ip + ":" + this.port;
    }

    static class HealthChecker extends HealthGrpc.HealthImplBase {
        public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
            HealthCheckResponse response = HealthCheckResponse.newBuilder()
                    .setStatus(HealthCheckResponse.ServingStatus.SERVING)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        public void watch(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        }
    }
}
