package com.sunpe.mygrpc.spring.server;

import com.sunpe.mygrpc.base.server.GrpcServer;
import com.sunpe.mygrpc.base.vo.GrpcServerConfig;
import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.List;
import java.util.Optional;

public class GrpcServerProcessor implements SmartLifecycle {

    private GrpcServer server;
    private final ServerProperties config;
    private final GrpcServiceDiscover discover;

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
        logger.info("start creating grpc server...");
        Optional<List<ServerServiceDefinition>> serviceDefinitions = getGrpcServices();
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
        logger.info("grpc server created and started");
        this.server = server;
    }

    private Optional<List<ServerServiceDefinition>> getGrpcServices() {
        return this.discover.discoverGrpcServices();
    }
}
