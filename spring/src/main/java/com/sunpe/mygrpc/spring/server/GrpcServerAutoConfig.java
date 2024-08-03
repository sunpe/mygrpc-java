package com.sunpe.mygrpc.spring.server;

import com.sunpe.mygrpc.spring.condition.ConditionOnAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * GrpcServerAutoConfig
 * grpc server auto config. If enable grpc server, it will start a grpc server thread.
 */
@Configuration
@EnableConfigurationProperties(ServerProperties.class)
@ConditionOnAnnotation(EnableGrpcServer.class)
public class GrpcServerAutoConfig {

    @Bean
    public GrpcServiceDiscover grpcServiceDiscover() {
        return new GrpcServiceDiscover();
    }

    @Bean
    public GrpcServerProcessor grpcServerLifecycle(@Autowired ServerProperties config, GrpcServiceDiscover grpcServiceDiscover) {
        return new GrpcServerProcessor(config, grpcServiceDiscover);
    }
}
