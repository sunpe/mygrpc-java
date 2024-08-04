package com.sunpe.mygrpc.spring.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;


/**
 * AnnotationCondition
 * It works with @{ConditionOnAnnotation.class} to check if it has special annotation in spring beans.
 */
public class AnnotationCondition implements Condition {

    private volatile boolean matched;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        if (matched) {
            return true;
        }
        if (!(metadata instanceof AnnotationMetadata)) {
            return false;
        }
        if (metadata.isAnnotated(ConditionOnAnnotation.class.getName())) {
            String className = ConditionOnAnnotation.class.getName();
            Map<String, Object> attributes = metadata.getAnnotationAttributes(className);
            assert attributes != null;
            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) attributes.get("value");
            try {
                String[] beanNames = Objects.requireNonNull(context.getBeanFactory()).getBeanNamesForAnnotation(annotationClass);
                if (beanNames.length == 0) {
                    return false;
                }
                logger.info("find annotation [{}]", className);
                matched = true;
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
