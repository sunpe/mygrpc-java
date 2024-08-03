package com.sunpe.mygrpc.spring.server;

import io.grpc.BindableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GrpcServiceDiscover implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Optional<Collection<BindableService>> discoverGrpcServices() {
        String[] beanNames = this.applicationContext.getBeanNamesForAnnotation(GrpcService.class);
        if (beanNames.length == 0) {
            return Optional.empty();
        }
        Set<BindableService> services = new HashSet<>(beanNames.length);
        for (String beanName : beanNames) {
            BindableService bindableService = this.applicationContext.getBean(beanName, BindableService.class);
            logger.debug("find grpc service definition bean name=[{}]", beanName);
            services.add(bindableService);
        }
        return Optional.of(services);
    }
}
