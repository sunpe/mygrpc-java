package com.sunpe.mygrpc.spring.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * grpc stub annotation.
 * The field to be annotation should be a implement of @{io.grpc.stub.AbstractStub}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface GrpcClient {
}
