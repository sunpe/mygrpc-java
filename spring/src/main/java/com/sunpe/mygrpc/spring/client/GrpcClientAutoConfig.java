package com.sunpe.mygrpc.spring.client;

import com.sunpe.mygrpc.spring.condition.ConditionOnAnnotation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionOnAnnotation(EnableGrpcClient.class)
@EnableConfigurationProperties(ClientProperties.class)
public class GrpcClientAutoConfig {

    @Bean
    public GrpcClientProcessor grpcClientProcessor(ClientProperties properties) {
        return new GrpcClientProcessor(properties);
    }
}
