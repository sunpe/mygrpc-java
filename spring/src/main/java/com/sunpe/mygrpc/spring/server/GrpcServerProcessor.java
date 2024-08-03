package com.sunpe.mygrpc.spring.server;

import com.sunpe.mygrpc.base.server.GrpcServer;
import com.sunpe.mygrpc.base.vo.GrpcServerConfig;
import io.grpc.BindableService;
import org.springframework.context.SmartLifecycle;

import java.util.Collection;
import java.util.Optional;

public class GrpcServerProcessor implements SmartLifecycle {

    private GrpcServer server;
    private final ServerProperties config;
    private final GrpcServiceDiscover discover;

    public GrpcServerProcessor(ServerProperties config, GrpcServiceDiscover discover) {
        this.config = config;
        this.discover = discover;
    }

    @Override
    public void start() {
        try {
            createAndStartGrpcServer();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start the grpc server", e);
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        return server != null && server.isRunning();
    }

    private void createAndStartGrpcServer() throws Exception {
        Optional<Collection<BindableService>> serviceDefinitions = getGrpcServices();
        if (serviceDefinitions.isEmpty()) {
            throw new IllegalStateException("No grpc services found");
        }
        GrpcServerConfig serverConfig = GrpcServerConfig.Builder.newBuilder()
                .withDiscoveryServiceClass(this.config.getServiceDiscoveryClass())
                .withGroup(this.config.getGroup())
                .withPort(this.config.getPort())
                .withRegistry(this.config.getRegistry())
                .build();

        GrpcServer server = new GrpcServer(serverConfig);
        server.bindServices(serviceDefinitions.get());
        server.start();
        this.server = server;
    }

    private Optional<Collection<BindableService>> getGrpcServices() {
        return this.discover.discoverGrpcServices();
    }
}
