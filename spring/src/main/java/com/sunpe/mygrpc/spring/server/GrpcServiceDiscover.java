package com.sunpe.mygrpc.spring.server;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GrpcServiceDiscover implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Optional<List<ServerServiceDefinition>> discoverGrpcServices() {
        String[] beanNames = this.applicationContext.getBeanNamesForAnnotation(GrpcService.class);
        if (beanNames.length == 0) {
            return Optional.empty();
        }
        List<ServerServiceDefinition> services = new ArrayList<>(beanNames.length);
        for (String beanName : beanNames) {
            BindableService bindableService = this.applicationContext.getBean(beanName, BindableService.class);
            logger.debug("find grpc service definition bean name=[{}]", beanName);
            services.add(bindableService.bindService());
        }
        return Optional.of(services);
    }
}
