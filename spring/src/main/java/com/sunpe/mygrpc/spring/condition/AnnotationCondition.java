package com.sunpe.mygrpc.spring.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.Map;


/**
 * AnnotationCondition
 * It works with @{ConditionOnAnnotation.class} to check if it has special annotation in spring beans.
 */
public class AnnotationCondition implements Condition {

    private volatile boolean matched;

    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        if (matched) {
            return true;
        }
        if (!(metadata instanceof AnnotationMetadata)) {
            return false;
        }
        if (metadata.isAnnotated(ConditionOnAnnotation.class.getName())) {
            Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionOnAnnotation.class.getName());
            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) attributes.get("value");
            try {
                String[] beanNames = context.getBeanFactory().getBeanNamesForAnnotation(annotationClass);
                if (beanNames.length == 0) {
                    return false;
                }
                matched = true;
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
