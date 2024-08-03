package com.sunpe.mygrpc.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ConditionOnAnnotation.
 * It works with @{Annotation} to check if it has special annotation in spring beans.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(AnnotationCondition.class)
public @interface ConditionOnAnnotation {
    Class<? extends Annotation> value();
}
