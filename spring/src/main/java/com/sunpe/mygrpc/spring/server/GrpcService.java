package com.sunpe.mygrpc.spring.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GrpcService
 * grpc service annotation, to label a service is a grpc service(It should a implement of @{io.grpc.BindableService}).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface GrpcService {
}
